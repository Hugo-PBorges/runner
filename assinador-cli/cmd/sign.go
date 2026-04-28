package cmd

import (
    "encoding/json"
    "fmt"
    "os"
    "os/exec"

    "github.com/spf13/cobra"
)

var inputPath string

var signCmd = &cobra.Command{
    Use:   "sign",
    Short: "Cria uma assinatura digital",
    Run: func(cmd *cobra.Command, args []string) {
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

        if servidorRodando() {
            fmt.Println("Servidor detectado, enviando via HTTP...")
            fazerRequisicao("http://localhost:8080/signature/sign", file)
        } else {
            fmt.Println("Servidor não detectado, executando cold start...")
            javaCmd := exec.Command(
                "java", "-jar", "assinador.jar",
                "sign",
                fmt.Sprintf("--input=%s", inputPath),
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
    rootCmd.AddCommand(signCmd)
    signCmd.Flags().StringVar(&inputPath, "input", "", "Caminho para o JSON de entrada")
    signCmd.MarkFlagRequired("input")
}