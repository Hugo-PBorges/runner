package cmd

import (
	"fmt"
	"os"
)

const uso = `simulador-cli — Gerenciador do HubSaúde Simulador

Comandos:
  start    Inicia o simulador
  stop     Para o simulador
  status   Exibe o status do simulador
`

func Execute() {
	if len(os.Args) < 2 {
		fmt.Print(uso)
		os.Exit(1)
	}

	switch os.Args[1] {
	case "start":
		cmdStart()
	case "stop":
		cmdStop()
	case "status":
		cmdStatus()
	default:
		fmt.Fprintf(os.Stderr, "Comando desconhecido: %q\n\n", os.Args[1])
		fmt.Print(uso)
		os.Exit(1)
	}
}