package cmd

import (
	"crypto/tls"
	"encoding/json"
	"fmt"
	"net/http"
	"time"
)

func cmdStatus() {
	if portaLivre(porta) {
		fmt.Println("● Simulador: NÃO está em execução")
		return
	}

	client := &http.Client{
		Timeout: 4 * time.Second,
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
		},
	}

	resp, err := client.Get(baseURL + "/api/info")
	if err != nil {
		fmt.Printf("● Simulador: porta %d ocupada, mas /api/info não respondeu\n   Erro: %v\n", porta, err)
		return
	}
	defer resp.Body.Close()

	fmt.Println("● Simulador: EM EXECUÇÃO")
	fmt.Printf("  URL: %s\n", baseURL)

	var info map[string]interface{}
	if err := json.NewDecoder(resp.Body).Decode(&info); err == nil {
		for _, k := range []string{"version", "status", "uptime"} {
			if v, ok := info[k]; ok {
				fmt.Printf("  %s: %v\n", k, v)
			}
		}
	}
}