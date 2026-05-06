package cmd

import (
	"fmt"
	"net"
	"os"
	"strconv"
	"strings"
	"time"
)

const (
	porta   = 8443
	baseURL = "https://localhost:8443"
	pidFile = "simulador.pid"
)

func portaLivre(p int) bool {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("localhost:%d", p), time.Second)
	if err != nil {
		return true
	}
	conn.Close()
	return false
}

func salvarPID(pid int) {
	os.WriteFile(pidFile, []byte(strconv.Itoa(pid)), 0644)
}

func carregarPID() int {
	data, err := os.ReadFile(pidFile)
	if err != nil {
		return 0
	}
	pid, _ := strconv.Atoi(strings.TrimSpace(string(data)))
	return pid
}