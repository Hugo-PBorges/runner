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
	Run: func(cmd *cobra.Command, args []string) {
		if !modoValido(validateMode) {
			fmt.Printf("Erro: --mode inválido (%q). Use: cold ou http\n", validateMode)
			return
		}

		if _, err := os.Stat(validationInputPath); os.IsNotExist(err) {
			fmt.Println("Erro: arquivo não encontrado:", validationInputPath)
			return
		}

		file, err := os.ReadFile(validationInputPath)
		if err != nil {
			fmt.Println("Erro ao ler arquivo:", err)
			return
		}

		var js map[string]interface{}
		if err := json.Unmarshal(file, &js); err != nil {
			fmt.Println("Erro: JSON inválido")
			return
		}

		executarOperacao(validateMode, "validate", validationInputPath, file)
	},
}

func init() {
	rootCmd.AddCommand(validateCmd)
	validateCmd.Flags().StringVar(&validationInputPath, "input", "", "Caminho para o JSON de entrada")
	validateCmd.Flags().StringVar(&validateMode, "mode", "", "Modo de execução (obrigatório): cold (cold start via java -jar) ou http (servidor)")
	validateCmd.MarkFlagRequired("input")
	validateCmd.MarkFlagRequired("mode")
}
