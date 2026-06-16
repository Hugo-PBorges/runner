package cmd

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var validationInputPath string
var validateMode string

var validateCmd = &cobra.Command{
	Use:   "validate",
	Short: "Valida uma assinatura digital",
	Long:  "Verifica a integridade e autenticidade de uma assinatura digital.",
	Example: `  assinador-cli validate --mode=http --input=val.json
  assinador-cli validate --mode=cold --input=val.json`,
	Run: func(cmd *cobra.Command, args []string) {
		if !modoValido(validateMode) {
			fmt.Fprintf(os.Stderr, "Erro: --mode inválido (%q). Use: cold ou http\n", validateMode)
			os.Exit(1)
			return
		}

		if _, err := os.Stat(validationInputPath); os.IsNotExist(err) {
			fmt.Fprintln(os.Stderr, "Erro: arquivo não encontrado:", validationInputPath)
			os.Exit(1)
			return
		}

		file, err := os.ReadFile(validationInputPath)
		if err != nil {
			fmt.Fprintln(os.Stderr, "Erro ao ler arquivo:", err)
			os.Exit(1)
			return
		}

		var js map[string]interface{}
		if err := json.Unmarshal(file, &js); err != nil {
			fmt.Fprintln(os.Stderr, "Erro: JSON inválido em", validationInputPath)
			os.Exit(1)
			return
		}

		executarOperacao(validateMode, "validate", validationInputPath, file)
	},
}

func init() {
	rootCmd.AddCommand(validateCmd)
	validateCmd.Flags().StringVar(&validationInputPath, "input", "", "Caminho para o JSON de entrada (obrigatório)")
	validateCmd.Flags().StringVar(&validateMode, "mode", "", "Modo de execução: cold ou http (obrigatório)")
	validateCmd.MarkFlagRequired("input")
	validateCmd.MarkFlagRequired("mode")
}
