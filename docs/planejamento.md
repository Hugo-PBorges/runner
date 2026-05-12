# Plano de Ação — Sistema Runner

## Visão Geral

O desenvolvimento do Sistema Runner será organizado em 4 sprints incrementais, com evolução contínua desde a base funcional até a entrega final do sistema completo.

O sistema é composto por:
- `assinador.jar` (Java 21) — núcleo de assinatura
- CLI de assinatura (Go) — orquestração do assinador
- CLI do simulador (Go) — gerenciamento do `simulador.jar`

---

## Sprint 1 — Fundação do Assinador

### Objetivo

Estabelecer a base funcional do `assinador.jar`, com suporte aos modos local e inicialização HTTP, além de iniciar o CLI em Go.

### Escopo

#### Assinador (`assinador.jar`)

- Estrutura inicial com Java 21 e Spring
- Definição dos parâmetros de entrada e saída (base FHIR, simulação)
- Implementação de `SignatureService` com `FakeSignatureService`
- Suporte aos modos:
  - CLI (execução local), com os métodos `version`, `sign` e `validate`
  - HTTP (`POST /signature/sign`, `POST /signature/validate`) apenas iniciado via CLI
- Validação inicial de parâmetros
- Padronização básica de saída (CLI e JSON)

#### CLI do Assinador (Go)

- Estrutura inicial do projeto
- Comandos básicos para invocação do `assinador.jar` (`sign` e `validate`)
- Sem infraestrutura avançada (JDK, processos, persistência)

### Resultado Esperado

- Execução básica de `sign` e `validate` para JSON com parâmetros
- Comunicação inicial entre CLI e `assinador.jar`
- Fluxo ponta a ponta funcional em nível inicial

---

## Sprint 2 — Validação e Consolidação

### Objetivo

- Fazer a primeira release do Assinador Simulator v0.1.0
- Fornecer os arquivos executáveis do assinador
- Criar forma de acessar o assinador no modo servidor
- Criar testes unitários

### Escopo

#### Assinador (`assinador.jar`)

- Consistência entre execução CLI e HTTP

#### CLI do Assinador (Go)

- Suporte ao modo servidor HTTP (padrão), para realizar requisições via CLI

### Resultado Esperado

- CLI operando de forma consistente nos dois modos (cold e HTTP)
- Simulação confiável e aderente ao escopo
- Fluxo ponta a ponta estável

---

## Sprint 3 — CLI do Simulador

### Objetivo

Consolidar o CLI do assinador e introduzir a primeira versão do CLI do Simulador do HubSaúde, garantindo gerenciamento básico do ciclo de vida e download automático de dependências.

### Escopo

#### CLI do Assinador (Go)

- Consolidação dos comandos:
  - `sign`, `validate`, `server`, `stop`
- Definição do modo padrão (servidor) no projeto do `assinador.cli`
- Melhoria de usabilidade (help, organização)
- Estudo e utilização do **SunPKCS11 provider** no projeto do `assinador.cli`

#### CLI do Simulador (Go) — Primeira Versão

- Implementação inicial dos comandos:
  - `start` — iniciar o simulador
  - `stop` — parar o simulador via endpoint `/shutdown`
  - `status` — exibir o status atual do simulador ou informar que não está em execução via endpoint `/api/info`
- Verificação se a porta padrão (8443) está disponível antes de iniciar o simulador
- Controle básico do ciclo de vida do simulador (iniciar, parar, status)

### Resultado Esperado

- CLI do assinador com SunPKCS11 provider
- CLI do Simulador HubSaúde funcional
- Binários disponíveis para download

---

## Sprint 4 — Integração Final e Entrega

### Objetivo

Mapear o provisionamento automático de JDK e das dependências no repositório, garantindo versões atualizadas.

### Escopo

- Provisionamento automático de JDK para rodar o `assinador.jar`
- Provisionamento do `hubsaude.jar` disponível no repositório da disciplina

#### Documentação

- Guia de instalação junto à release
- Descrição dos fluxos e estrutura

#### Entrega

- Consolidação dos artefatos gerados
- Validação final dos binários

### Resultado Esperado

- Sistema completamente integrado
- Ambos os CLIs funcionando de forma consistente
- Fluxos ponta a ponta validados
- Projeto documentado e pronto para uso

---

## Resumo das Sprints

| Sprint | Foco Principal | Entregável |
|--------|----------------|------------|
| 1 | Fundação | Fluxo básico funcionando |
| 2 | Validação e robustez | Assinador consolidado |
| 3 | CLI, simulador | CLIs e distribuíveis |
| 4 | Provisionamento e entrega | Sistema completo |

---
