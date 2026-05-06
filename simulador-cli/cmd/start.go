package cmd

import (
	"fmt"
	"os"
	"os/exec"
)

const jar = "hubsaude-simulador-0.0.0-SNAPSHOT.jar"

func cmdStart() {

	if !portaLivre(porta) {
		fmt.Printf("	Porta %d já está em uso — simulador pode já estar rodando.\n", porta)
		fmt.Println("   Use 'simulador-cli status' para verificar.")
		os.Exit(1)
	}


	if _, err := os.Stat(jar); err != nil {
		fmt.Fprintf(os.Stderr, "Erro: arquivo %q não encontrado no diretório atual.\n", jar)
		os.Exit(1)
	}

	fmt.Printf("Iniciando %s...\n", jar)

	proc := exec.Command("java", "-jar", jar)
	proc.Stdout = os.Stdout
	proc.Stderr = os.Stderr

	if err := proc.Start(); err != nil {
		fmt.Fprintf(os.Stderr, "Erro ao iniciar o simulador: %v\n", err)
		os.Exit(1)
	}

	salvarPID(proc.Process.Pid)
	fmt.Printf("Simulador iniciado  |  PID: %d  |  https://localhost:%d\n", proc.Process.Pid, porta)

	proc.Wait()
}