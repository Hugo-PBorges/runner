# Plano de Ação — Sistema Runner

## Visão Geral

O desenvolvimento do Sistema Runner foi organizado em 4 sprints incrementais, com evolução contínua desde a base funcional até a entrega final do sistema completo.

O sistema é composto por:
- `assinador.jar` (Java 21 + Spring Boot) — núcleo de assinatura digital via PKCS#11
- `assinador-cli` (Go + Cobra) — orquestração do `assinador.jar`
- `simulador-cli` (Go + Cobra) — gerenciamento do `simulador.jar`

---

## Sprint 1 — Fundação do Assinador
**Período:** maio/2025  
**Status:** ✅ Concluído

### O que foi feito

#### assinador.jar
- Estrutura inicial com Java 21 e Spring Boot
- Definição dos DTOs de entrada: `SignRequestDTO`, `BundleDTO`, `ProvenanceDTO`, `CryptoDTO`
- Validação inicial de campos obrigatórios com Bean Validation
- Suporte aos modos de execução: CLI (cold start) e HTTP (`POST /sign`, `POST /validate`)
- Padronização básica de saída no formato `OperationOutcome`

#### Documentação
- Arquitetura conceitual do sistema definida
- Estrutura inicial do repositório

---

## Sprint 2 — Validação, Serviços e Refatoração
**Período:** maio–junho/2025  
**Status:** ✅ Concluído

### O que foi feito

#### assinador.jar
- Reestruturação de pacotes: `cli/`, `controller/`, `service/`, `config/`, `exception/`, `shutdown/`, `validator/`
- Remoção do `FakeSignatureService` — substituído pela implementação real com PKCS#11
- Implementação do `Pkcs11SignatureService` com SunPKCS11 e SHA256withRSA
- Exceções tipadas: `BusinessValidationException` (erros do usuário) e `CryptographicException` (erros do sistema)
- `GlobalExceptionHandler` com respostas HTTP padronizadas (400, 422, 500)
- `SignValidator` validando consistência entre `bundle.entries` e `provenance.target`
- Idle shutdown automático: `IdleShutdownService` encerra o servidor após inatividade configurável
- `ActivityTracker` e `ActivityInterceptor` para rastrear última requisição recebida
- Configuração PKCS#11 externalizada em `Pkcs11Properties` e `application.yaml`
- Suite de testes: `SignValidatorTest`, `SignatureControllerTest`, `Pkcs11SignatureServiceTest` e validações de todos os DTOs

---

## Sprint 3 — CLIs, Simulador e Infraestrutura
**Período:** junho/2025  
**Status:** ✅ Concluído

### O que foi feito

#### assinador-cli
- Migração para Cobra com subcomandos: `sign`, `validate`, `shutdown`, `version`
- Flag `--mode` obrigatória (`cold` | `http`) — modo de execução explícito
- Flag `--port` global para configurar a porta do servidor (padrão: 8080)
- Modo `http`: inicia o servidor automaticamente se não estiver rodando
- Modo `cold`: executa `assinador.jar` diretamente como subprocess
- Provisionamento automático de JDK 21 via Eclipse Temurin / Adoptium API em `~/.hubsaude/jdk/`
- Resolução do `assinador.jar` relativo ao executável — funciona de qualquer diretório
- Comando `shutdown` para encerrar o servidor via `/actuator/shutdown`
- Stdout e stderr separados; exit code propagado do JAR
- Comando `version` com SHA do commit rastreável

#### simulador-cli
- Implementação dos subcomandos: `start`, `stop`, `status`, `version`
- Download automático do `simulador.jar` via GitHub Releases com cache local em `~/.hubsaude/simulador/jars/`
- Verificação de integridade via sidecar SHA-256; fallback para cache offline
- Persistência de estado (PID, porta, jar, timestamp) em `~/.hubsaude/simulador/state.json`
- `start` com readiness check — aguarda o servidor estar pronto antes de retornar
- `stop` com graceful shutdown via `/shutdown` e fallback por kill de PID
- `status` exibindo PID, jar, URL e informações do `/api/info`

#### Infraestrutura do repositório
- `.gitignore` para Java, Go e IDE; `.gitattributes` com line endings LF
- `LICENSE` MIT
- `.github/workflows/ci.yml` — CI em Windows e Linux para Java (Maven) e Go (build + vet)
- ADRs: porta padrão (ADR-001), provisionamento de JDK (ADR-002), framework CLI Cobra (ADR-003)
- `go.sum` gerado para `assinador-cli` e `simulador-cli`

---

## Sprint 4 — Integração Final e Qualidade
**Período:** junho/2026  
**Status:** 🔄 Em andamento

### O que foi feito

- README reescrito como contrato do projeto (build, testes, uso, estrutura, como contribuir)
- Correção de regressão no `utils.go` causada por merge indevido de `main` em `dev`
- Revert do merge e restauração do estado correto da `dev`

### Pendente
- Branch protection no GitHub (`main` e `dev`) para bloquear merge sem CI verde
