package cmd

import (
	"fmt"
	"time"

	"simulador-cli/internal/registry"

	"github.com/spf13/cobra"
)

var stopCmd = &cobra.Command{
	Use:   "stop",
	Short: "Para o simulador",
	Run: func(cmd *cobra.Command, args []string) {
		state, hasState := registry.Load()

		if portaLivre(porta) {
			fmt.Println("Simulador não está em execução.")
			_ = registry.Clear()
			return
		}

		fmt.Println("Enviando shutdown...")
		client := httpsClient(5 * time.Second)
		resp, err := client.Post(baseURL+"/shutdown", "application/json", nil)
		if err != nil {
			fmt.Println("Aviso: erro ao chamar /shutdown:", err)
		} else {
			resp.Body.Close()
		}

		if waitPortFree(porta, 5*time.Second) {
			fmt.Println("Simulador encerrado.")
			_ = registry.Clear()
			return
		}

		if hasState && state.PID > 0 {
			fmt.Println("Endpoint /shutdown não respondeu a tempo. Encerrando processo PID", state.PID)
			if err := killProcess(state.PID); err != nil {
				fmt.Println("Erro ao encerrar processo:", err)
			}
		}

		if waitPortFree(porta, 3*time.Second) {
			fmt.Println("Simulador encerrado.")
		} else {
			fmt.Println("Aviso: não foi possível confirmar o encerramento do simulador.")
		}
		_ = registry.Clear()
	},
}

func init() {
	rootCmd.AddCommand(stopCmd)
}
