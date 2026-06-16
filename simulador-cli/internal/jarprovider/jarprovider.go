// Package jarprovider resolve o caminho local do simulador.jar, baixando-o
// automaticamente da release mais recente disponível em
// https://github.com/kyriosdata/runner/releases quando necessário.
package jarprovider

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"sort"
	"strings"
)

const (
	releasesURL = "https://api.github.com/repos/kyriosdata/runner/releases"
	tagFilter   = "simulador"
)

type asset struct {
	Name               string `json:"name"`
	BrowserDownloadURL string `json:"browser_download_url"`
}

type release struct {
	TagName     string  `json:"tag_name"`
	PublishedAt string  `json:"published_at"`
	Assets      []asset `json:"assets"`
}

// EnsureJar retorna o caminho local do simulador.jar, baixando-o se
// necessário.
//
//   - Se source != "", baixa (ou reusa do cache) o jar a partir dessa URL.
//   - Caso contrário, consulta o GitHub Releases de kyriosdata/runner pela
//     release mais recente cujo tag contenha "simulador", reaproveitando o
//     cache local em ~/.hubsaude/simulador/jars/ quando já baixado.
//   - Se a consulta ao GitHub falhar (ex.: sem internet) e houver algum jar
//     em cache, esse jar é reutilizado como fallback.
func EnsureJar(source string) (string, error) {
	if source != "" {
		return ensureFromURL(source, "custom")
	}

	rel, jarAsset, err := latestSimuladorRelease()
	if err != nil {
		if cached, ok := newestCachedJar(); ok {
			fmt.Println("Aviso: não foi possível consultar o GitHub Releases (" + err.Error() + ").")
			fmt.Println("Usando jar em cache:", cached)
			return cached, nil
		}
		return "", fmt.Errorf("não foi possível obter o simulador.jar: %w", err)
	}

	return ensureFromURL(jarAsset.BrowserDownloadURL, rel.TagName)
}

// ensureFromURL garante que o arquivo apontado por url esteja em
// ~/.hubsaude/simulador/jars/<dir>/<nomeDoArquivo>, baixando-o se ausente.
func ensureFromURL(url, dir string) (string, error) {
	cacheDir, err := jarsDir()
	if err != nil {
		return "", err
	}

	targetDir := filepath.Join(cacheDir, dir)
	jarPath := filepath.Join(targetDir, filepath.Base(url))

	if _, err := os.Stat(jarPath); err == nil {
		return jarPath, nil
	}

	if err := os.MkdirAll(targetDir, 0o755); err != nil {
		return "", err
	}

	fmt.Println("simulador.jar não encontrado em cache. Baixando de:", url)
	if err := downloadFile(url, jarPath); err != nil {
		return "", fmt.Errorf("erro ao baixar %s: %w", url, err)
	}

	if err := writeChecksumSidecar(jarPath); err != nil {
		fmt.Println("Aviso: não foi possível gravar checksum do jar:", err)
	}

	fmt.Println("Download concluído:", jarPath)
	return jarPath, nil
}

// latestSimuladorRelease consulta a lista de releases do repositório e
// retorna a mais recente cujo tag_name contenha "simulador" e que possua
// um asset .jar.
func latestSimuladorRelease() (release, asset, error) {
	resp, err := http.Get(releasesURL)
	if err != nil {
		return release{}, asset{}, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return release{}, asset{}, fmt.Errorf("status inesperado %d ao consultar %s", resp.StatusCode, releasesURL)
	}

	var releases []release
	if err := json.NewDecoder(resp.Body).Decode(&releases); err != nil {
		return release{}, asset{}, err
	}

	var candidates []release
	for _, r := range releases {
		if strings.Contains(strings.ToLower(r.TagName), tagFilter) {
			candidates = append(candidates, r)
		}
	}

	if len(candidates) == 0 {
		return release{}, asset{}, fmt.Errorf("nenhuma release de simulador encontrada em %s", releasesURL)
	}

	sort.Slice(candidates, func(i, j int) bool {
		return candidates[i].PublishedAt > candidates[j].PublishedAt
	})

	for _, r := range candidates {
		for _, a := range r.Assets {
			if strings.HasSuffix(strings.ToLower(a.Name), ".jar") {
				return r, a, nil
			}
		}
	}

	return release{}, asset{}, fmt.Errorf("release %q não possui asset .jar", candidates[0].TagName)
}

// newestCachedJar percorre o cache local e retorna o .jar modificado mais
// recentemente, se houver algum.
func newestCachedJar() (string, bool) {
	cacheDir, err := jarsDir()
	if err != nil {
		return "", false
	}

	var newestPath string
	var newestModTime int64
	_ = filepath.WalkDir(cacheDir, func(path string, d os.DirEntry, err error) error {
		if err != nil || d.IsDir() {
			return nil
		}
		if !strings.HasSuffix(strings.ToLower(d.Name()), ".jar") {
			return nil
		}
		info, err := d.Info()
		if err != nil {
			return nil
		}
		if mod := info.ModTime().Unix(); mod > newestModTime {
			newestModTime = mod
			newestPath = path
		}
		return nil
	})

	if newestPath == "" {
		return "", false
	}
	return newestPath, true
}

func jarsDir() (string, error) {
	home, err := os.UserHomeDir()
	if err != nil {
		return "", err
	}
	return filepath.Join(home, ".hubsaude", "simulador", "jars"), nil
}

func downloadFile(url, dest string) error {
	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("status inesperado %d", resp.StatusCode)
	}

	out, err := os.Create(dest)
	if err != nil {
		return err
	}
	defer out.Close()

	_, err = io.Copy(out, resp.Body)
	return err
}

// writeChecksumSidecar calcula o SHA-256 do jar baixado e grava em um
// arquivo "<jar>.sha256" ao lado dele. Isso permite detectar corrupção do
// cache local em execuções futuras (verificação "trust on first use" — o
// repositório de origem não publica checksums assinados para o
// simulador.jar no momento).
func writeChecksumSidecar(jarPath string) error {
	f, err := os.Open(jarPath)
	if err != nil {
		return err
	}
	defer f.Close()

	h := sha256.New()
	if _, err := io.Copy(h, f); err != nil {
		return err
	}

	checksum := hex.EncodeToString(h.Sum(nil))
	sidecar := jarPath + ".sha256"
	content := fmt.Sprintf("%s  %s\n", checksum, filepath.Base(jarPath))
	return os.WriteFile(sidecar, []byte(content), 0o644)
}
