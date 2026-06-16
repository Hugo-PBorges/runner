# ADR-003 — Framework CLI: Cobra (Go)

**Status:** aceito  
**Data:** 2025-06-15

## Contexto

Os CLIs (`assinador-cli` e `simulador-cli`) precisam de:
- Subcomandos (`sign`, `validate`, `start`, `stop`, `status`)
- Flags tipadas com validação e `--help` gerado automaticamente
- Binário único sem runtime externo (facilita distribuição)
- Build multiplataforma (Windows + Linux + macOS) via `GOOS`/`GOARCH`

## Decisão

Usar **Go 1.22** com o framework **[Cobra](https://github.com/spf13/cobra)**.

Alternativas consideradas:

| Opção | Descartado porque |
|-------|-------------------|
| Python + Click | Requer runtime Python; versionamento de intérprete é fricção extra para usuários finais |
| Java (Picocli) | Adicionaria dependência de JVM ao CLI, quebrando o objetivo de bootstrapping sem Java |
| Go (flag stdlib) | Subcomandos exigem boilerplate manual; `--help` menos ergonômico |

Cobra é o padrão de fato em CLIs Go (kubectl, Hugo, etc.), com API estável e suporte ativo.

## Consequências

- Cada subcomando é um arquivo separado em `cmd/`, com `init()` registrando no root.
- `--help` e `--version` são gerados automaticamente pelo Cobra.
- A flag `--port` é declarada como `PersistentFlag` no root para estar disponível em todos os subcomandos.
