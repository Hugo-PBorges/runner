# Assinador e Simulator CLI - v1.1.0

## Em andamento
- **SunPKCS11**: o estudo do provider `SunPKCS11` foi realizado e a integração está em andamento porém ainda nebulosa, com foco em entender como interagir com tokens e smart cards via interface PKCS#11 no Java.
- **Provisionamento de JRE**: o objetivo é entender e implementar o provisionamento automático do JRE. Esse problema não é trivial — envolve detectar a versão do Java disponível no sistema, lidar com diferenças entre plataformas (Windows, Linux, macOS)

## O que ja foi feito
- assinador.jar com os metodos sign e request e testes unitarios
- assinador cli com suporte para os metodos de start, cold e http ( http ainda nao incluido na release )
- assinador cli com os metodos sign request e version
- simulador cli do husaude com os metodos basicos `sign`, `validate`, `server`, `stop` ( ainda nao incluido na release )

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

## Guia Rápido de Uso

Para instruções completas, exemplos avançados e detalhes adicionais, consulte o README da release para mais detalhes:

[Assinador Simulator - v1.0.0](https://github.com/Hugo-PBorges/runner/releases/tag/v1.0.0)

### Passos básicos para testar a release

1. Coloque o `assinador-cli` e o `assinador.jar` na mesma pasta  
2. Crie um arquivo JSON de entrada (`sign` ou `validate`) com a estrutura compativel
3. Execute via terminal conforme o comando desejado

# Integrante

| Curso                   | Nome                 | Matrícula    |
|-------------------------|--------------------|-------------|
| Engenharia de Software  | Hugo Pereira Borges | 202403075   |

---

# Plano de Ação – Sistema Runner

O planejamento do **Sistema Runner** foi definido e pode ser consultado com todos os detalhes em: [Plano de Ação](https://github.com/Hugo-PBorges/runner/blob/main/docs/planejamento.md).

O sistema é composto por:
- `assinador.jar` (Java 21) — núcleo de assinatura digital
- CLI do assinador (Go) — orquestração do `assinador.jar`
- CLI do simulador (Go) — gerenciamento do `simulador.jar`


