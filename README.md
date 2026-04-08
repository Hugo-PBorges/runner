# Integrante

| Curso                   | Nome                 | Matrícula    |
|-------------------------|--------------------|-------------|
| Engenharia de Software  | Hugo Pereira Borges | 202403075   |

---


# A ser feito pós aula do dia 07/04
- Atualizar o documento de plnajamento
- Definir as tasks prioritarias para entregar valor até o fim da sprint 1
  ## Duvidas Retiradas
  - Não vai entrar o parametro 8 - Configurações operacionais definido na criação da assinatura definido no padrão FHIR
  - O CLI em modo HTTPS vai poder fazer requisições para o modo servidor de forma direta e não simplesmente disponibilizar o localhost/8080

# Plano de Ação – Sistema Runner

O planejamento do **Sistema Runner** foi definido e pode ser consultado com todos os detalhes em: [Plano de Ação](https://github.com/Hugo-PBorges/runner/blob/main/docs/planejamento.md).

O sistema é composto por:
- `assinador.jar` (Java 21) — núcleo de assinatura digital
- CLI do assinador (Go) — orquestração do `assinador.jar`
- CLI do simulador (Go) — gerenciamento do `simulador.jar`
