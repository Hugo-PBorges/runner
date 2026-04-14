package cmd

import (
	"os"

	"github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
	Use:   "assinatura",
	Short: "CLI para execução do assinador",
	Long:  "Ferramenta CLI para criação e validação de assinaturas digitais",
}

func Execute() {
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}