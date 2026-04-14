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

---

### Escopo

#### Assinador (`assinador.jar`)

- Estrutura inicial com Java 21 e Spring
- Definição dos parâmetros de entrada e saída (base FHIR, simulação)
- Implementação de `SignatureService` com `FakeSignatureService`
- Suporte aos modos:
  - CLI (execução local), com os métodos version, sign e validate
  - HTTP (`POST /signature/sign`, `POST /signature/validate`) apenas iniciado via CLI
- Validação inicial de parâmetros
- Padronização básica de saída (CLI e JSON)

#### CLI do Assinador (Go)

- Estrutura inicial do projeto
- Comandos básicos para invocação do `assinador.jar` sign e validate
- Sem infraestrutura avançada (JDK, processos, persistência)

---

### Resultado Esperado

- Execução básica de `sign` e `validate` para JSON com parametros
- Comunicação inicial entre CLI e `assinador.jar`
- Fluxo ponta a ponta funcional em nível inicial

---

## Sprint 2 — Validação e Consolidação

### Objetivo

Fazer a primeira realise do Assinador Simulator v1.0.0
Criar testes unitários e testes de integração

---

### Escopo

#### Assinador (`assinador.jar`)

- Verificar a viabilidade de testar o metodo de assinatura via SunPKCS11 provider
- Consistência entre execução CLI e HTTP

#### CLI do Assinador (Go)

- Suporte ao modo:
  - Servidor HTTP (padrão), para realizar requisições

---

### Resultado Esperado

- CLI operando de forma consistente nos dois modos Cold e HTTP
- Simulação confiável e aderente ao escopo
- Fluxo ponta a ponta estável

---

## Sprint 3 — CLI, Simulador e Infraestrutura

### Objetivo

Consolidar o CLI do assinador, introduzir o CLI do simulador e estruturar a infraestrutura necessária para execução autônoma e distribuição.

---

### Escopo

#### CLI do Assinador (Go)

- Consolidação dos comandos:
  - `sign`, `validate`, `server`, `stop`
- Definição do modo padrão (servidor)
- Melhoria de usabilidade (help, organização)

#### CLI do Simulador (Go)

- Implementação dos comandos:
  - `start`, `stop`, `status`
- Gerenciamento do ciclo de vida do `simulador.jar`
- Controle de processos, portas e estado local
- Download automático do simulador e controle de versão

#### Infraestrutura

- Gerenciamento de processos (`assinador.jar` e `simulador.jar`)
- Comunicação HTTP padronizada
- Manipulação de arquivos locais
- Detecção e controle de portas

#### Persistência local

- Estrutura `~/.hubsaude/`
- Armazenamento de:
  - PID
  - Porta
  - Runtime
  - Estado das aplicações

#### Provisionamento de dependências

- Detecção de JDK instalado
- Download automático do JDK quando necessário
- Configuração local para uso transparente pelos CLIs

#### CI/CD (GitHub)

- Configuração de pipelines de build e testes
- Execução automática a cada alteração no repositório
- Geração de binários multiplataforma:
  - Windows
  - Linux
  - macOS
- Publicação automática no GitHub Releases
- Versionamento e geração de checksums

---

### Resultado Esperado

- CLI do assinador completo e estável
- CLI do simulador funcional
- Execução independente de configuração manual
- Pipelines CI/CD configurados e operacionais
- Binários disponíveis para download

---

## Sprint 4 — Integração Final e Entrega

### Objetivo

Finalizar a integração entre todos os componentes, validar o sistema ponta a ponta e preparar a entrega com documentação completa.

---

### Escopo

#### Integração

- Integração completa entre:
  - CLI do assinador e `assinador.jar`
  - CLI do simulador e `simulador.jar`
- Execução dos fluxos completos:
  - Criação de assinatura
  - Validação de assinatura
- Padronização final de comportamento e mensagens

#### Testes

- Execução de testes abrangentes em todo o sistema
- Validação de cenários principais e de erro
- Testes ponta a ponta em ambiente controlado

#### Documentação

- Guia de instalação
- Manual de uso dos CLIs
- Exemplos de execução
- Documentação técnica da integração
- Descrição dos fluxos e arquitetura

#### Entrega

- Consolidação dos artefatos gerados
- Validação final dos binários
- Preparação para uso e distribuição

---

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
| 3 | CLI, simulador e infraestrutura | CLIs e distribuíveis |
| 4 | Integração e entrega | Sistema  |
