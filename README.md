# Assinador e Simulator CLI - v1.1.0

## Em andamento
- **SunPKCS11**: o estudo do provider `SunPKCS11` foi realizado e a integração está em andamento porém ainda nebulosa, com foco em entender como interagir com tokens e smart cards via interface PKCS#11 no Java.
- **Provisionamento de JRE**: o objetivo é entender e implementar o provisionamento automático do JRE. Esse problema não é trivial — envolve detectar a versão do Java disponível no sistema, lidar com diferenças entre plataformas (Windows, Linux, macOS)

## O que já foi feito
- `assinador.jar` com os métodos `sign` e `request` e testes unitários
- `assinador-cli` com suporte para os métodos `start`, `cold` e `http` (http ainda não incluído na release)
- `assinador-cli` com os métodos `sign`, `request` e `version`
- `simulador-cli` do HubSaúde com os métodos básicos `start`, `stop` e `status`

## Visão Geral da Release

Esta release expande o **Assinador Simulator** com a adição do **`simulador-cli`**, uma nova interface de linha de comando em Go para gerenciar o ciclo de vida do Simulador do HubSaúde (`hubsaude-simulador.jar`).

---

## Artefatos da Release

**assinador-cli**
- `assinador-cli-1.1.0-linux-amd64`
- `assinador-cli-1.1.0-macos-amd64`
- `assinador-cli-1.1.0-windows-amd64.exe`
- `assinador.jar`

**simulador-cli** *(novo)*
- `simulador-cli-1.1.0-linux-amd64`
- `simulador-cli-1.1.0-macos-amd64`
- `simulador-cli-1.1.0-windows-amd64.exe`
- `hubsaude-simulador-0.0.0-SNAPSHOT.jar`

---

## Novidades

### `simulador-cli` — Gerenciador do HubSaúde Simulador *(novo componente)*

Interface CLI para gerenciar o ciclo de vida do Simulador do HubSaúde sem necessidade de conhecer os comandos Java subjacentes.

#### Estrutura de Execução

O `simulador-cli` e o `hubsaude-simulador-0.0.0-SNAPSHOT.jar` devem estar na mesma pasta (root).

#### Comandos disponíveis

**`start`** — Inicia o simulador
```bash
simulador-cli start
```
- Verifica se a porta `8443` está disponível antes de iniciar
- Inicia o `hubsaude-simulador.jar` como subprocesso Java
- Salva o PID do processo em `simulador.pid`

**`stop`** — Para o simulador
```bash
simulador-cli stop
```
- Envia requisição `POST /shutdown` para encerrar o simulador graciosamente

**`status`** — Exibe o status atual do simulador
```bash
simulador-cli status
```
- Verifica se a porta `8443` está ocupada
- Consulta `GET /api/info` e exibe `version`, `status` e `uptime` se disponíveis

---

## Limitações da v1.1.0

- Assinatura simulada (não utiliza certificado real)
- `assinador-cli` e `assinador.jar` devem permanecer no mesmo diretório
- `simulador-cli` e `hubsaude-simulador.jar` devem permanecer no mesmo diretório
---

## Guia Rápido de Uso

Para instruções completas, exemplos avançados e detalhes adicionais, consulte o README da release:
[Assinador Simulator - v1.1.0](https://github.com/Hugo-PBorges/runner/releases/tag/v1.1.0)

### Passos básicos para testar a release

**assinador-cli:**
1. Coloque o `assinador-cli` e o `assinador.jar` na mesma pasta
2. Crie um arquivo JSON de entrada (`sign` ou `validate`) com a estrutura compatível
3. Execute via terminal conforme o comando desejado

**simulador-cli:**
1. Coloque o `simulador-cli` e o `hubsaude-simulador-0.0.0-SNAPSHOT.jar` na mesma pasta
2. Execute `simulador-cli start` para iniciar o simulador
3. Use `simulador-cli status` para verificar o estado e `simulador-cli stop` para encerrar

---

# Integrante

| Curso                  | Nome                 | Matrícula  |
|------------------------|----------------------|------------|
| Engenharia de Software | Hugo Pereira Borges  | 202403075  |

---

# Plano de Ação – Sistema Runner

O planejamento do **Sistema Runner** foi definido e pode ser consultado com todos os detalhes em: [Plano de Ação](https://github.com/Hugo-PBorges/runner/blob/main/docs/planejamento.md).

O sistema é composto por:
- `assinador.jar` (Java 21) — núcleo de assinatura digital
- CLI do assinador (Go) — orquestração do `assinador.jar`
- CLI do simulador (Go) — gerenciamento do `simulador.jar`
