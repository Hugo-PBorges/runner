package cmd

import (
	"encoding/json"
	"fmt"
	"os"
	"os/exec"

	"github.com/spf13/cobra"
)

var validationInputPath string

var validateCmd = &cobra.Command{
	Use:   "validate",
	Short: "Valida uma assinatura digital",
	Run: func(cmd *cobra.Command, args []string) {
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

		if servidorRodando() {
			fmt.Println("Servidor detectado, enviando via HTTP...")
			fazerRequisicao("http://localhost:8080/signature/validate", file)
		} else {
			fmt.Println("Servidor não detectado, executando cold start...")
			javaCmd := exec.Command(
				"java", "-jar", "assinador.jar",
				"validate",
				fmt.Sprintf("--input=%s", validationInputPath),
			)
			output, err := javaCmd.CombinedOutput()
			fmt.Println(string(output))
			if err != nil {
				fmt.Println("Erro ao executar:", err)
			}
		}
	},
}

func init() {
	rootCmd.AddCommand(validateCmd)
	validateCmd.Flags().StringVar(&validationInputPath, "input", "", "Caminho para o JSON de entrada")
	validateCmd.MarkFlagRequired("input")
}