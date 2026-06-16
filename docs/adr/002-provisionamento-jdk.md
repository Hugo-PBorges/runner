# ADR-002 — Provisionamento automático de JDK

**Status:** aceito  
**Data:** 2025-06-15

## Contexto

O `assinador.jar` requer Java 21+. Não se pode assumir que o usuário final tenha o JDK instalado ou na versão correta. O CLI deve funcionar em uma máquina limpa sem intervenção manual.

## Decisão

O `assinador-cli` detecta o Java disponível na seguinte ordem:

1. `~/.hubsaude/jdk/` — JDK gerenciado pelo próprio CLI (instalação prévia).
2. `java` no `PATH` do sistema, desde que a versão seja >= 21.
3. Download automático do **Eclipse Temurin 21** via [Adoptium API](https://api.adoptium.net/) e extração em `~/.hubsaude/jdk/`.

**Eclipse Temurin** foi escolhido porque:
- É a distribuição de referência da Eclipse Foundation, com builds reproduzíveis.
- A Adoptium API fornece URL de download estável por versão, OS e arquitetura.
- Licença GPLv2+CE — compatível com uso interno e redistribuição.

O diretório `~/.hubsaude/` centraliza todos os artefatos gerenciados (JDK, jars, estado) para facilitar limpeza e diagnóstico.

## Consequências

- Primeiro cold start em máquina sem Java 21 pode levar ~1 min (download ~200 MB).
- Instâncias subsequentes reutilizam o JDK em cache — sem download.
- Arquiteturas suportadas: `amd64` e `aarch64` em Windows, Linux e macOS.
- Arquiteturas não suportadas recebem erro explícito em vez de falha silenciosa.
