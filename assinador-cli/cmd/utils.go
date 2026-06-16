package cmd

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"time"

	"assinador-cli/internal/jdk"
)

const (
	ModeCold = "cold"
	ModeHTTP = "http"
)

// serverPort é definido via flag --port no root command.
var serverPort int

func baseURL() string {
	return fmt.Sprintf("http://localhost:%d", serverPort)
}

func modoValido(mode string) bool {
	switch mode {
	case ModeCold, ModeHTTP:
		return true
	default:
		return false
	}
}

func servidorRodando() bool {
	client := http.Client{Timeout: 2 * time.Second}
	resp, err := client.Get(baseURL() + "/actuator/health")
	if err != nil {
		return false
	}
	defer resp.Body.Close()
	return resp.StatusCode == http.StatusOK
}

func fazerRequisicao(endpoint string, body []byte) {
	url := baseURL() + "/signature/" + endpoint
	resp, err := http.Post(url, "application/json", bytes.NewBuffer(body))
	if err != nil {
		fmt.Fprintln(os.Stderr, "Erro na requisição:", err)
		fmt.Fprintln(os.Stderr, "Verifique se o servidor está em execução e acessível em", baseURL())
		os.Exit(1)
		return
	}
	defer resp.Body.Close()

	respBody, _ := io.ReadAll(resp.Body)

	var pretty map[string]interface{}
	if err := json.Unmarshal(respBody, &pretty); err != nil {
		fmt.Println(string(respBody))
		return
	}
	out, _ := json.MarshalIndent(pretty, "", "  ")
	fmt.Println(string(out))
}

func executarOperacao(mode, command string, inputPath string, file []byte) {
	switch mode {
	case ModeHTTP:
		if !servidorRodando() {
			fmt.Println("Servidor não está em execução. Iniciando assinador.jar em modo HTTP...")
			iniciarServidor()
		}
		fmt.Println("Modo http: enviando requisição para", baseURL())
		fazerRequisicao(command, file)
	case ModeCold:
		fmt.Println("Modo cold: executando assinador.jar diretamente...")
		executarColdStart(command, inputPath)
	}
}

// iniciarServidor sobe o assinador.jar em background e aguarda o servidor estar pronto.
func iniciarServidor() {
	javaPath, err := jdk.EnsureJava()
	if err != nil {
		fmt.Fprintln(os.Stderr, "Erro ao provisionar JDK:", err)
		os.Exit(1)
	}

	jarPath := resolveJarPath()
	if _, statErr := os.Stat(jarPath); os.IsNotExist(statErr) {
		fmt.Fprintf(os.Stderr, "Erro: assinador.jar não encontrado em %s\n", jarPath)
		fmt.Fprintln(os.Stderr, "Certifique-se de que assinador.jar está no mesmo diretório que este executável.")
		os.Exit(1)
	}

	proc := exec.Command(javaPath,
		fmt.Sprintf("-Dserver.port=%d", serverPort),
		"-jar", jarPath,
	)
	proc.Stdout = os.Stdout
	proc.Stderr = os.Stderr
	if err := proc.Start(); err != nil {
		fmt.Fprintln(os.Stderr, "Erro ao iniciar o servidor:", err)
		os.Exit(1)
	}

	fmt.Printf("Servidor iniciado (PID %d). Aguardando ficar pronto", proc.Process.Pid)
	if !waitServerReady(60 * time.Second) {
		fmt.Fprintln(os.Stderr, "\nErro: servidor não respondeu em 60s.")
		_ = proc.Process.Kill()
		os.Exit(1)
	}
	fmt.Println(" OK")
}

func waitServerReady(timeout time.Duration) bool {
	deadline := time.Now().Add(timeout)
	for time.Now().Before(deadline) {
		conn, err := net.DialTimeout("tcp", fmt.Sprintf("localhost:%d", serverPort), time.Second)
		if err == nil {
			conn.Close()
			if servidorRodando() {
				return true
			}
		}
		fmt.Print(".")
		time.Sleep(500 * time.Millisecond)
	}
	return false
}

// resolveJarPath localiza assinador.jar no mesmo diretório do executável atual,
// garantindo funcionamento independente do diretório de trabalho.
func resolveJarPath() string {
	exe, err := os.Executable()
	if err != nil {
		return "assinador.jar"
	}
	return filepath.Join(filepath.Dir(exe), "assinador.jar")
}

func executarColdStart(command, inputPath string) {
	javaPath, err := jdk.EnsureJava()
	if err != nil {
		fmt.Fprintln(os.Stderr, "Erro ao provisionar JDK:", err)
		fmt.Fprintln(os.Stderr, "Instale o Java 21+ manualmente ou verifique sua conexão com a internet.")
		os.Exit(1)
		return
	}

	jarPath := resolveJarPath()
	if _, statErr := os.Stat(jarPath); os.IsNotExist(statErr) {
		fmt.Fprintf(os.Stderr, "Erro: assinador.jar não encontrado em %s\n", jarPath)
		fmt.Fprintln(os.Stderr, "Certifique-se de que assinador.jar está no mesmo diretório que este executável.")
		os.Exit(1)
		return
	}

	javaCmd := exec.Command(javaPath, "-jar", jarPath, command, fmt.Sprintf("--input=%s", inputPath))
	javaCmd.Stdout = os.Stdout
	javaCmd.Stderr = os.Stderr

	if err := javaCmd.Run(); err != nil {
		if javaCmd.ProcessState != nil && javaCmd.ProcessState.ExitCode() != 0 {
			os.Exit(javaCmd.ProcessState.ExitCode())
		}
		os.Exit(1)
	}
}
