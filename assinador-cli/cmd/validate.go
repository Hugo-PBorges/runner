package cmd

import (
	"fmt"
	"os"
	"os/exec"
	"encoding/json"

	"github.com/spf13/cobra"
)

var (
	validationInputPath string
)

var validateCmd = &cobra.Command{
	Use:   "validate",
	Short: "Valida uma assinatura digital",
	Run: func(cmd *cobra.Command, args []string) {
		if validationInputPath == "" {
			fmt.Println("Erro: --validationInputPath é obrigatório")
			return
		}

		fmt.Println("Lendo arquivo JSON...")

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

		fmt.Println("Executando assinador Java...")

		javaCmd := exec.Command(
			"java",
			"-jar",
			"assinador.jar",
			"validate",
			fmt.Sprintf("--input=%s", validationInputPath),
		)

		output, err := javaCmd.CombinedOutput()

		fmt.Println("Resultado:")
		fmt.Println(string(output))

		if err != nil {
			fmt.Println("Erro ao executar:", err)
		}

	},
}

func init() {
	rootCmd.AddCommand(validateCmd)

	validateCmd.Flags().StringVar(&validationInputPath, "input", "", "Caminho para o JSON de entrada")
	validateCmd.MarkFlagRequired("input")
}