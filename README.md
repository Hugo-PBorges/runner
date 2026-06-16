# Runner — Assinador Digital HubSaúde

| Curso                  | Nome                 | Matrícula  |
|------------------------|----------------------|------------|
| Engenharia de Software | Hugo Pereira Borges  | 202403075  |

Implementação do sistema **Runner** conforme especificação em
[kyriosdata/runner](https://github.com/kyriosdata/runner).

[![CI](https://github.com/HugoPBorges/runner/actions/workflows/ci.yml/badge.svg)](https://github.com/HugoPBorges/runner/actions/workflows/ci.yml)

---

## O que é

Sistema composto por três módulos:

| Módulo           | Tecnologia             | Responsabilidade                                |
|------------------|------------------------|-------------------------------------------------|
| `assinador/`     | Java 21 + Spring Boot  | Núcleo de assinatura digital via PKCS#11        |
| `assinador-cli/` | Go 1.22 + Cobra        | Orquestra o `assinador.jar` (HTTP ou cold start)|
| `simulador-cli/` | Go 1.22 + Cobra        | Gerencia o ciclo de vida do `simulador.jar`     |

---

## Pré-requisitos

- **Java 21+** — ou deixe o `assinador-cli` provisionar automaticamente ([ADR-002](docs/adr/002-provisionamento-jdk.md))
- **Go 1.22+**
- **Maven 3.9+**
- **SoftHSM2** — para testes de assinatura PKCS#11

---

## Build

### assinador.jar

```bash
cd assinador
mvn -B package -DskipTests
# Saída: target/assinador-*.jar  →  copiar como assinador.jar ao lado do CLI
```

### assinador-cli

```bash
cd assinador-cli
go build -ldflags "-X assinador-cli/cmd.Version=$(git describe --tags --always)" -o assinador-cli .
```

### simulador-cli

```bash
cd simulador-cli
go build -ldflags "-X main.Version=$(git describe --tags --always)" -o simulador-cli .
```

---

## Testes

```bash
# Java — suite completa (101 testes)
cd assinador
mvn -B verify

# Go — lint estático
cd assinador-cli && go vet ./...
cd simulador-cli  && go vet ./...
```

> Testes de integração PKCS#11 requerem SoftHSM2 instalado e configurado.

---

## Uso

### Assinador

```bash
# Coloque assinador.jar e assinador-cli no mesmo diretório

# Inicia servidor em :8080
./assinador-cli

# Assinar via servidor HTTP
./assinador-cli sign --mode=http --input=req.json

# Assinar via cold start (sem servidor)
./assinador-cli sign --mode=cold --input=req.json

# Validar assinatura
./assinador-cli validate --mode=http --input=val.json

# Porta alternativa
./assinador-cli --port=9090 sign --mode=http --input=req.json

# Ver versão
./assinador-cli version
```

### Simulador HubSaúde

```bash
./simulador-cli start             # baixa simulador.jar e inicia em :8443
./simulador-cli status            # exibe PID, URL e versão
./simulador-cli stop              # encerra com graceful shutdown
./simulador-cli version
```

---

## Estrutura

```
runner/
├── assinador/          # Java — Spring Boot, PKCS#11
├── assinador-cli/      # Go — CLI orquestrador
├── simulador-cli/      # Go — CLI do simulador
├── docs/
│   └── adr/            # Decisões de arquitetura
├── .github/workflows/  # CI: build + testes em Windows e Linux
├── .gitattributes
├── .gitignore
└── LICENSE
```

---

## Decisões de Arquitetura (ADRs)

- [ADR-001 — Portas padrão](docs/adr/001-porta-padrao.md)
- [ADR-002 — Provisionamento de JDK](docs/adr/002-provisionamento-jdk.md)
- [ADR-003 — Framework CLI (Cobra)](docs/adr/003-framework-cli.md)
