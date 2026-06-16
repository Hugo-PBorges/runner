# ADR-001 — Portas padrão dos serviços

**Status:** aceito  
**Data:** 2025-06-15

## Contexto

O sistema possui dois serviços com ciclos de vida independentes:
- `assinador.jar` — Spring Boot, modo HTTP
- `simulador.jar` — HubSaúde Simulador

Ambos precisam de uma porta padrão documentada para que o CLI e os testes possam localizar os serviços sem configuração manual.

## Decisão

| Serviço       | Porta padrão | Protocolo |
|---------------|-------------|-----------|
| assinador.jar | 8080        | HTTP      |
| simulador.jar | 8443        | HTTPS     |

A porta 8080 é a padrão do Spring Boot (`server.port`) e não requer configuração adicional.  
A porta 8443 é usada pelo simulador pois ele expõe HTTPS nativamente; 443 exigiria privilégios de root.

A porta do `assinador.jar` é configurável via `application.yaml` (`server.port`) e pode ser sobrescrita com a flag `--port` do CLI. A porta do simulador é fixa na versão atual do `simulador.jar` (não expõe flag de configuração).

## Consequências

- O CLI do assinador aceita `--port` para apontar para instâncias em portas alternativas.
- Testes que sobem servidor real devem explicitamente declarar a porta usada.
- Conflito de porta (`address already in use`) produz mensagem de erro com instrução de resolução.
