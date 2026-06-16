package cmd

import (
	"os"

	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "assinador-cli",
	Short: "CLI para execução do assinador digital",
	Long: `Ferramenta CLI para criação e validação de assinaturas digitais.

Sem argumentos, inicia o assinador.jar em modo servidor HTTP (porta padrão: 8080).
Use --port para apontar para um servidor em porta diferente.

Exemplos:
  assinador-cli                              # inicia servidor em :8080
  assinador-cli sign     --mode=http --input=req.json
  assinador-cli validate --mode=cold --input=val.json
  assinador-cli --port=9090 sign --mode=http --input=req.json`,
}

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		os.Exit(1)
	}
}

func init() {
	rootCmd.PersistentFlags().IntVar(&serverPort, "port", 8080,
		"Porta do servidor assinador (modo http)")
}
