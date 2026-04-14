# Assinador Simulator - v1.0.0

## Visão Geral

Esta primeira release disponibiliza a primeira versão funcional do **Assinador Simulator**, composta por dois componentes principais:

- `assinador.jar`: aplicação Java responsável pela lógica de assinatura e validação  
- `assinador-cli`: interface de linha de comando desenvolvida em Go para execução dos métodos via terminal  

Atualmente, o sistema funciona exclusivamente em **modo CLI (cold start)**, onde cada execução inicia o processo do assinador Java sob demanda.

---

## Artefatos da Release

- `assinador-cli-1.0.0-linux-amd64`
- `assinador-cli-1.0.0-macos-amd64` 
- `assinador-cli-1.0.0-windows-amd64.exe`
- `assinador.jar` 

---

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


# A ser feito pós aula do dia 14/04
- Fazer testes unitários e de integração para o assinador.jar
- Testar o metodo de assinatura via SunPKCS11 provider
- Servidor HTTP (padrão), para o assinador-cli realizar requisições para a API exposta pelo assinador.jar

# Plano de Ação – Sistema Runner

O planejamento do **Sistema Runner** foi definido e pode ser consultado com todos os detalhes em: [Plano de Ação](https://github.com/Hugo-PBorges/runner/blob/main/docs/planejamento.md).

O sistema é composto por:
- `assinador.jar` (Java 21) — núcleo de assinatura digital
- CLI do assinador (Go) — orquestração do `assinador.jar`
- CLI do simulador (Go) — gerenciamento do `simulador.jar`


