// Package jdk provisiona automaticamente um JDK 21 para execução do assinador.jar
// quando nenhuma instalação compatível é encontrada no sistema.
package jdk

import (
	"archive/tar"
	"archive/zip"
	"compress/gzip"
	"fmt"
	"io"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"regexp"
	"runtime"
	"strconv"
)

const requiredMajorVersion = 21

// EnsureJava garante que um binário java >= 21 esteja disponível, retornando
// o caminho completo para o executável. Se necessário, baixa e instala o
// Eclipse Temurin 21 em ~/.hubsaude/jdk/.
func EnsureJava() (string, error) {
	if path, ok := managedJavaPath(); ok {
		return path, nil
	}

	if path, ok := systemJavaPath(); ok {
		return path, nil
	}

	fmt.Println("JDK 21 não encontrado. Provisionando automaticamente...")
	return downloadAndInstall()
}

// managedJavaPath verifica se já existe um JDK provisionado em ~/.hubsaude/jdk/.
func managedJavaPath() (string, bool) {
	dir, err := jdkBaseDir()
	if err != nil {
		return "", false
	}

	javaBin := javaBinaryName()
	var found string
	_ = filepath.WalkDir(dir, func(path string, d os.DirEntry, err error) error {
		if err != nil || found != "" {
			return nil
		}
		if !d.IsDir() && d.Name() == javaBin && filepath.Base(filepath.Dir(path)) == "bin" {
			found = path
		}
		return nil
	})

	if found == "" {
		return "", false
	}
	return found, true
}

// systemJavaPath verifica se existe um java no PATH com versão >= 21.
func systemJavaPath() (string, bool) {
	path, err := exec.LookPath("java")
	if err != nil {
		return "", false
	}

	version, err := javaMajorVersion(path)
	if err != nil || version < requiredMajorVersion {
		return "", false
	}
	return path, true
}

var versionRegex = regexp.MustCompile(`version "(\d+)`)

func javaMajorVersion(javaPath string) (int, error) {
	out, err := exec.Command(javaPath, "-version").CombinedOutput()
	if err != nil {
		return 0, err
	}

	match := versionRegex.FindSubmatch(out)
	if match == nil {
		return 0, fmt.Errorf("não foi possível identificar a versão do java")
	}
	return strconv.Atoi(string(match[1]))
}

func javaBinaryName() string {
	if runtime.GOOS == "windows" {
		return "java.exe"
	}
	return "java"
}

func jdkBaseDir() (string, error) {
	home, err := os.UserHomeDir()
	if err != nil {
		return "", err
	}
	return filepath.Join(home, ".hubsaude", "jdk"), nil
}

// downloadAndInstall baixa o Eclipse Temurin JDK 21 da Adoptium API e o
// extrai para ~/.hubsaude/jdk/, retornando o caminho do binário java.
func downloadAndInstall() (string, error) {
	baseDir, err := jdkBaseDir()
	if err != nil {
		return "", fmt.Errorf("não foi possível resolver diretório do usuário: %w", err)
	}

	if err := os.MkdirAll(baseDir, 0o755); err != nil {
		return "", fmt.Errorf("erro ao criar diretório %s: %w", baseDir, err)
	}

	url, archiveExt, err := temurinDownloadURL()
	if err != nil {
		return "", err
	}

	fmt.Println("Baixando JDK 21 de:", url)
	archivePath := filepath.Join(baseDir, "download."+archiveExt)
	if err := downloadFile(url, archivePath); err != nil {
		return "", fmt.Errorf("erro ao baixar JDK: %w", err)
	}
	defer os.Remove(archivePath)

	fmt.Println("Extraindo JDK em:", baseDir)
	if archiveExt == "zip" {
		err = extractZip(archivePath, baseDir)
	} else {
		err = extractTarGz(archivePath, baseDir)
	}
	if err != nil {
		return "", fmt.Errorf("erro ao extrair JDK: %w", err)
	}

	path, ok := managedJavaPath()
	if !ok {
		return "", fmt.Errorf("JDK extraído mas binário java não foi encontrado em %s", baseDir)
	}

	if runtime.GOOS != "windows" {
		_ = os.Chmod(path, 0o755)
	}

	fmt.Println("JDK 21 provisionado com sucesso em:", path)
	return path, nil
}

// temurinDownloadURL monta a URL da API da Adoptium para o JDK 21 (hotspot)
// de acordo com o sistema operacional e arquitetura atuais.
func temurinDownloadURL() (url string, archiveExt string, err error) {
	var os_ string
	switch runtime.GOOS {
	case "windows":
		os_, archiveExt = "windows", "zip"
	case "linux":
		os_, archiveExt = "linux", "tar.gz"
	case "darwin":
		os_, archiveExt = "mac", "tar.gz"
	default:
		return "", "", fmt.Errorf("sistema operacional não suportado para provisionamento de JDK: %s", runtime.GOOS)
	}

	var arch string
	switch runtime.GOARCH {
	case "amd64":
		arch = "x64"
	case "arm64":
		arch = "aarch64"
	default:
		return "", "", fmt.Errorf("arquitetura não suportada para provisionamento de JDK: %s", runtime.GOARCH)
	}

	url = fmt.Sprintf(
		"https://api.adoptium.net/v3/binary/latest/%d/ga/%s/%s/jdk/hotspot/normal/eclipse",
		requiredMajorVersion, os_, arch,
	)
	return url, archiveExt, nil
}

func downloadFile(url, dest string) error {
	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("status inesperado %d ao baixar %s", resp.StatusCode, url)
	}

	out, err := os.Create(dest)
	if err != nil {
		return err
	}
	defer out.Close()

	_, err = io.Copy(out, resp.Body)
	return err
}

func extractZip(archivePath, destDir string) error {
	r, err := zip.OpenReader(archivePath)
	if err != nil {
		return err
	}
	defer r.Close()

	for _, f := range r.File {
		targetPath := filepath.Join(destDir, f.Name)

		if f.FileInfo().IsDir() {
			if err := os.MkdirAll(targetPath, 0o755); err != nil {
				return err
			}
			continue
		}

		if err := os.MkdirAll(filepath.Dir(targetPath), 0o755); err != nil {
			return err
		}

		if err := extractZipFile(f, targetPath); err != nil {
			return err
		}
	}
	return nil
}

func extractZipFile(f *zip.File, targetPath string) error {
	rc, err := f.Open()
	if err != nil {
		return err
	}
	defer rc.Close()

	out, err := os.OpenFile(targetPath, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, f.Mode())
	if err != nil {
		return err
	}
	defer out.Close()

	_, err = io.Copy(out, rc)
	return err
}

func extractTarGz(archivePath, destDir string) error {
	file, err := os.Open(archivePath)
	if err != nil {
		return err
	}
	defer file.Close()

	gzr, err := gzip.NewReader(file)
	if err != nil {
		return err
	}
	defer gzr.Close()

	tr := tar.NewReader(gzr)
	for {
		header, err := tr.Next()
		if err == io.EOF {
			return nil
		}
		if err != nil {
			return err
		}

		targetPath := filepath.Join(destDir, header.Name)

		switch header.Typeflag {
		case tar.TypeDir:
			if err := os.MkdirAll(targetPath, 0o755); err != nil {
				return err
			}
		case tar.TypeReg:
			if err := os.MkdirAll(filepath.Dir(targetPath), 0o755); err != nil {
				return err
			}
			out, err := os.OpenFile(targetPath, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, os.FileMode(header.Mode))
			if err != nil {
				return err
			}
			if _, err := io.Copy(out, tr); err != nil {
				out.Close()
				return err
			}
			out.Close()
		}
	}
}
