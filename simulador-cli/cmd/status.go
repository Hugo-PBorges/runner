package cmd

import (
	"encoding/json"
	"fmt"
	"time"

	"simulador-cli/internal/registry"

	"github.com/spf13/cobra"
)

var statusCmd = &cobra.Command{
	Use:   "status",
	Short: "Exibe o status do simulador",
	Run: func(cmd *cobra.Command, args []string) {
		state, hasState := registry.Load()

		if portaLivre(porta) {
			fmt.Println("● Simulador: NÃO está em execução")
			if hasState {
				_ = registry.Clear()
			}
			return
		}

		fmt.Println("● Simulador: EM EXECUÇÃO")
		fmt.Printf("  URL: %s\n", baseURL)
		if hasState {
			fmt.Printf("  PID: %d\n", state.PID)
			fmt.Printf("  Jar: %s\n", state.JarPath)
			fmt.Printf("  Iniciado em: %s\n", state.StartedAt.Format(time.RFC3339))
		}

		client := httpsClient(4 * time.Second)
		resp, err := client.Get(baseURL + "/api/info")
		if err != nil {
			fmt.Printf("  Aviso: /api/info não respondeu: %v\n", err)
			return
		}
		defer resp.Body.Close()

		var info map[string]interface{}
		if err := json.NewDecoder(resp.Body).Decode(&info); err == nil {
			for _, k := range []string{"version", "status", "uptime"} {
				if v, ok := info[k]; ok {
					fmt.Printf("  %s: %v\n", k, v)
				}
			}
		}
	},
}

func init() {
	rootCmd.AddCommand(statusCmd)
}
