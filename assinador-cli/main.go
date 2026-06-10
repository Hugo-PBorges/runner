package main

import (
    "fmt"
    "assinador-cli/cmd"
    "assinador-cli/internal/jdk"
    "os"
    "os/exec"
)

func main() {
    if len(os.Args) == 1 {
        fmt.Println("Nenhum comando passado, iniciando o assinador em modo servidor...")

        javaPath, err := jdk.EnsureJava()
        if err != nil {
            fmt.Println("Erro ao provisionar JDK:", err)
            os.Exit(1)
        }

        javaCmd := exec.Command(javaPath, "-jar", "assinador.jar")
        javaCmd.Stdout = os.Stdout
        javaCmd.Stderr = os.Stderr

        if err := javaCmd.Start(); err != nil {
            fmt.Println("Erro ao iniciar o JAR:", err)
            os.Exit(1)
        }

        fmt.Println("Servidor iniciado. PID:", javaCmd.Process.Pid)
        fmt.Println("Use './assinador-cli sign --input=req.json' para assinar")
        return
    }
    cmd.Execute()
}
