package cmd

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os/exec"
	"time"

	"assinador-cli/internal/jdk"
)

const (
	ModeCold = "cold"
	ModeHTTP = "http"
)

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
	resp, err := client.Get("http://localhost:8080/actuator/health")
	if err != nil {
		return false
	}
	defer resp.Body.Close()
	return resp.StatusCode == http.StatusOK
}

func fazerRequisicao(url string, body []byte) {
	resp, err := http.Post(url, "application/json", bytes.NewBuffer(body))
	if err != nil {
		fmt.Println("Erro na requisição:", err)
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

func executarOperacao(mode, command, inputPath string, file []byte) {
	url := fmt.Sprintf("http://localhost:8080/signature/%s", command)

	switch mode {
	case ModeHTTP:
		if !servidorRodando() {
			fmt.Println("Erro: servidor não está em execução em http://localhost:8080 (--mode=http)")
			return
		}
		fmt.Println("Modo http: enviando requisição...")
		fazerRequisicao(url, file)
	case ModeCold:
		fmt.Println("Modo cold: executando assinador.jar diretamente...")
		executarColdStart(command, inputPath)
	}
}

func executarColdStart(command, inputPath string) {
	javaPath, err := jdk.EnsureJava()
	if err != nil {
		fmt.Println("Erro ao provisionar JDK:", err)
		return
	}

	javaCmd := exec.Command(
		javaPath, "-jar", "assinador.jar",
		command,
		fmt.Sprintf("--input=%s", inputPath),
	)
	output, err := javaCmd.CombinedOutput()
	fmt.Println(string(output))
	if err != nil {
		fmt.Println("Erro ao executar:", err)
	}
}
