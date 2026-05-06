package cmd

import (
	"crypto/tls"
	"fmt"
	"net/http"
	"os"
	"time"
)

func cmdStop() {
	if portaLivre(porta) {
		fmt.Println("Simulador não está em execução.")
		return
	}

	fmt.Println("Enviando shutdown...")

	client := &http.Client{
		Timeout: 5 * time.Second,
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
		},
	}

	resp, err := client.Post(baseURL+"/shutdown", "application/json", nil)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Erro ao chamar /shutdown: %v\n", err)
		os.Exit(1)
	}
	resp.Body.Close()

	fmt.Println("Comando de shutdown enviado.")
}