package cmd

import (
	"fmt"
	"net/http"
	"os"
	"time"

	"github.com/spf13/cobra"
)

var shutdownCmd = &cobra.Command{
	Use:   "shutdown",
	Short: "Encerra o servidor assinador",
	Example: `  assinador-cli shutdown
  assinador-cli --port=9090 shutdown`,
	Run: func(cmd *cobra.Command, args []string) {
		if !servidorRodando() {
			fmt.Printf("Nenhum servidor encontrado em %s\n", baseURL())
			os.Exit(1)
		}

		url := baseURL() + "/actuator/shutdown"
		fmt.Printf("Enviando shutdown para %s ...\n", baseURL())

		client := &http.Client{Timeout: 5 * time.Second}
		resp, err := client.Post(url, "application/json", nil)
		if err != nil {
			fmt.Fprintln(os.Stderr, "Erro ao enviar shutdown:", err)
			os.Exit(1)
		}
		resp.Body.Close()

		fmt.Print("Aguardando encerramento")
		deadline := time.Now().Add(15 * time.Second)
		for time.Now().Before(deadline) {
			if !servidorRodando() {
				fmt.Println(" OK")
				fmt.Println("Servidor encerrado.")
				return
			}
			fmt.Print(".")
			time.Sleep(500 * time.Millisecond)
		}

		fmt.Fprintln(os.Stderr, "\nAviso: servidor não encerrou em 15s. Tente manualmente:")
		fmt.Fprintf(os.Stderr, "  netstat -ano | findstr :%d\n", serverPort)
		fmt.Fprintln(os.Stderr, "  taskkill /PID <pid> /F")
		os.Exit(1)
	},
}

func init() {
	rootCmd.AddCommand(shutdownCmd)
}
