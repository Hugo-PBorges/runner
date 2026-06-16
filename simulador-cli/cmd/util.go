package cmd

import (
	"crypto/tls"
	"fmt"
	"net"
	"net/http"
	"os"
	"time"
)

// waitReady aguarda até o servidor aceitar conexões TCP na porta p.
// Imprime "." a cada tentativa para indicar progresso.
func waitReady(p int, timeout time.Duration) bool {
	deadline := time.Now().Add(timeout)
	for time.Now().Before(deadline) {
		if !portaLivre(p) {
			return true
		}
		fmt.Print(".")
		time.Sleep(500 * time.Millisecond)
	}
	return false
}

const (
	porta   = 8443
	baseURL = "https://localhost:8443"
)

// portaLivre indica se a porta informada está livre (ninguém escutando nela).
func portaLivre(p int) bool {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("localhost:%d", p), time.Second)
	if err != nil {
		return true
	}
	conn.Close()
	return false
}

// waitPortFree aguarda até timeout para a porta ficar livre, retornando true
// se isso aconteceu dentro do prazo.
func waitPortFree(p int, timeout time.Duration) bool {
	deadline := time.Now().Add(timeout)
	for time.Now().Before(deadline) {
		if portaLivre(p) {
			return true
		}
		time.Sleep(300 * time.Millisecond)
	}
	return portaLivre(p)
}

// killProcess encerra o processo com o PID informado. Retorna nil também
// quando o processo já não existe.
func killProcess(pid int) error {
	proc, err := os.FindProcess(pid)
	if err != nil {
		return nil
	}
	if err := proc.Kill(); err != nil {
		return err
	}
	return nil
}

func httpsClient(timeout time.Duration) *http.Client {
	return &http.Client{
		Timeout: timeout,
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
		},
	}
}
