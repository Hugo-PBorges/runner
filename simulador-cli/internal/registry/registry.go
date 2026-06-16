// Package registry persiste o estado do simulador (PID, porta e jar em uso)
// em ~/.hubsaude/simulador/state.json, permitindo que comandos independentes
// (start/stop/status) compartilhem essa informação entre execuções.
package registry

import (
	"encoding/json"
	"os"
	"path/filepath"
	"time"
)

type State struct {
	PID       int       `json:"pid"`
	Port      int       `json:"port"`
	JarPath   string    `json:"jarPath"`
	StartedAt time.Time `json:"startedAt"`
}

func statePath() (string, error) {
	home, err := os.UserHomeDir()
	if err != nil {
		return "", err
	}
	return filepath.Join(home, ".hubsaude", "simulador", "state.json"), nil
}

// Save grava o estado atual do simulador em disco.
func Save(state State) error {
	path, err := statePath()
	if err != nil {
		return err
	}

	if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
		return err
	}

	data, err := json.MarshalIndent(state, "", "  ")
	if err != nil {
		return err
	}

	return os.WriteFile(path, data, 0o644)
}

// Load lê o estado salvo. O segundo retorno é false se não houver
// estado registrado (simulador nunca iniciado ou registro já limpo).
func Load() (State, bool) {
	path, err := statePath()
	if err != nil {
		return State{}, false
	}

	data, err := os.ReadFile(path)
	if err != nil {
		return State{}, false
	}

	var state State
	if err := json.Unmarshal(data, &state); err != nil {
		return State{}, false
	}

	return state, true
}

// Clear remove o registro de estado, indicando que o simulador não está
// mais sendo gerenciado por este CLI.
func Clear() error {
	path, err := statePath()
	if err != nil {
		return err
	}

	err = os.Remove(path)
	if err != nil && !os.IsNotExist(err) {
		return err
	}
	return nil
}
