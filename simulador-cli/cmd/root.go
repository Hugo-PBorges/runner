package cmd

import (
	"os"

	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "simulador-cli",
	Short: "CLI para gerenciar o Simulador do HubSaúde",
	Long: `Ferramenta CLI para iniciar, parar e consultar o status do Simulador do HubSaúde.

O simulador.jar é baixado automaticamente do GitHub Releases na primeira execução
e armazenado em ~/.hubsaude/simulador/jars/. Execuções subsequentes usam o cache.

Exemplos:
  simulador-cli start            # baixa e inicia o simulador
  simulador-cli status           # exibe status, PID e versão
  simulador-cli stop             # encerra com graceful shutdown`,
}

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		os.Exit(1)
	}
}
