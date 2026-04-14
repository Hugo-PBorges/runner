package main

import (
	"fmt"
	"assinador-cli/cmd"
	"os"
	"os/exec"
)

func main() {
	if len(os.Args) == 1 {
        fmt.Println("Nenhum comando passado, iniciando o assinador em modo padrão...")
        javaCmd := exec.Command(
            "java",
            "-jar",
            "assinador.jar",
        )
        javaCmd.Stdout = os.Stdout
        javaCmd.Stderr = os.Stderr

        if err := javaCmd.Run(); err != nil {
            fmt.Println("Erro ao iniciar o JAR:", err)
            os.Exit(1)
        }
        return
	}
	cmd.Execute()
}