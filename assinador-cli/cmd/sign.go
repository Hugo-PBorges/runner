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
    Run: func(cmd *cobra.Command, args []string) {
        if !modoValido(signMode) {
            fmt.Printf("Erro: --mode inválido (%q). Use: cold ou http\n", signMode)
            return
        }

        if _, err := os.Stat(inputPath); os.IsNotExist(err) {
            fmt.Println("Erro: arquivo não encontrado:", inputPath)
            return
        }

        file, err := os.ReadFile(inputPath)
        if err != nil {
            fmt.Println("Erro ao ler arquivo:", err)
            return
        }

        var js map[string]interface{}
        if err := json.Unmarshal(file, &js); err != nil {
            fmt.Println("Erro: JSON inválido")
            return
        }

        executarOperacao(signMode, "sign", inputPath, file)
    },
}

func init() {
    rootCmd.AddCommand(signCmd)
    signCmd.Flags().StringVar(&inputPath, "input", "", "Caminho para o JSON de entrada")
    signCmd.Flags().StringVar(&signMode, "mode", "", "Modo de execução (obrigatório): cold (cold start via java -jar) ou http (servidor)")
    signCmd.MarkFlagRequired("input")
    signCmd.MarkFlagRequired("mode")
}
