package cmd

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/spf13/cobra"
)

var inputPath string
var signMode string

var signCmd = &cobra.Command{
	Use:   "sign",
	Short: "Cria uma assinatura digital",
	Long:  "Envia um bundle FHIR para assinatura digital via PKCS#11.",
	Example: `  assinador-cli sign --mode=http --input=req.json
  assinador-cli sign --mode=cold --input=req.json
  assinador-cli --port=9090 sign --mode=http --input=req.json`,
	Run: func(cmd *cobra.Command, args []string) {
		if !modoValido(signMode) {
			fmt.Fprintf(os.Stderr, "Erro: --mode inválido (%q). Use: cold ou http\n", signMode)
			os.Exit(1)
			return
		}

		if _, err := os.Stat(inputPath); os.IsNotExist(err) {
			fmt.Fprintln(os.Stderr, "Erro: arquivo não encontrado:", inputPath)
			os.Exit(1)
			return
		}

		file, err := os.ReadFile(inputPath)
		if err != nil {
			fmt.Fprintln(os.Stderr, "Erro ao ler arquivo:", err)
			os.Exit(1)
			return
		}

		var js map[string]interface{}
		if err := json.Unmarshal(file, &js); err != nil {
			fmt.Fprintln(os.Stderr, "Erro: JSON inválido em", inputPath)
			os.Exit(1)
			return
		}

		executarOperacao(signMode, "sign", inputPath, file)
	},
}

func init() {
	rootCmd.AddCommand(signCmd)
	signCmd.Flags().StringVar(&inputPath, "input", "", "Caminho para o JSON de entrada (obrigatório)")
	signCmd.Flags().StringVar(&signMode, "mode", "", "Modo de execução: cold ou http (obrigatório)")
	signCmd.MarkFlagRequired("input")
	signCmd.MarkFlagRequired("mode")
}
