package cmd

import (
	"fmt"
	"os"
	"os/exec"
	"time"

	"simulador-cli/internal/jarprovider"
	"simulador-cli/internal/registry"

	"github.com/spf13/cobra"
)

var source string

var startCmd = &cobra.Command{
	Use:   "start",
	Short: "Inicia o simulador",
	Long:  "Baixa (se necessário) e inicia o simulador.jar. Aguarda o servidor estar pronto antes de retornar.",
	Example: `  simulador-cli start
  simulador-cli start --source=https://example.com/simulador.jar`,
	Run: func(cmd *cobra.Command, args []string) {
		if !portaLivre(porta) {
			fmt.Printf("Porta %d já está em uso — simulador pode já estar rodando.\n", porta)
			fmt.Println("Use 'simulador-cli status' para verificar.")
			os.Exit(1)
		}

		jarPath, err := jarprovider.EnsureJar(source)
		if err != nil {
			fmt.Fprintln(os.Stderr, "Erro ao obter simulador.jar:", err)
			os.Exit(1)
		}

		fmt.Println("Iniciando", jarPath, "...")

		proc := exec.Command("java", "-jar", jarPath)
		proc.Stdout = os.Stdout
		proc.Stderr = os.Stderr

		if err := proc.Start(); err != nil {
			fmt.Fprintln(os.Stderr, "Erro ao iniciar o simulador:", err)
			os.Exit(1)
		}

		fmt.Print("Aguardando simulador ficar pronto")
		if !waitReady(porta, 60*time.Second) {
			fmt.Fprintln(os.Stderr, "\nErro: simulador não respondeu em 60s.")
			fmt.Fprintln(os.Stderr, "Verifique se o Java está instalado e o jar é válido.")
			_ = proc.Process.Kill()
			os.Exit(1)
		}
		fmt.Println(" OK")

		state := registry.State{
			PID:       proc.Process.Pid,
			Port:      porta,
			JarPath:   jarPath,
			StartedAt: time.Now(),
		}
		if err := registry.Save(state); err != nil {
			fmt.Fprintln(os.Stderr, "Aviso: não foi possível registrar o estado em ~/.hubsaude/:", err)
		}

		fmt.Printf("Simulador iniciado  |  PID: %d  |  %s\n", proc.Process.Pid, baseURL)
		fmt.Println("Use 'simulador-cli status' para consultar e 'simulador-cli stop' para encerrar.")
	},
}

func init() {
	rootCmd.AddCommand(startCmd)
	startCmd.Flags().StringVar(&source, "source", "", "URL alternativa para baixar o simulador.jar (padrão: release mais recente do GitHub)")
}
