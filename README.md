# Runner
Projeto para a disciplina de Implementação e Integração de Software

---

## Integrante
- Curso: Engenharia de Software
- Nome: Hugo Pereira Borges
- Matrícula: 202403075

---

## Documentação

- [Documento de Arquitetura Conceitual](docs/Arquitetura_Conceitual.pdf)

Este documento representa a versão inicial da arquitetura conceitual do sistema, apresentando uma visão geral e ainda superficial dos principais mecanismos, responsabilidades e interações entre os componentes.

---

## 1. Visão Geral

O Sistema Runner tem como objetivo facilitar a execução de aplicações Java por meio de linha de comando, abstraindo do usuário detalhes relacionados à configuração de ambiente e à execução manual de arquivos `.jar`.

---

## 2. Arquitetura Conceitual

O sistema é estruturado em três camadas, com responsabilidades superficialmente definidas:

### Camada de Entrada — CLI
- Recebe comandos do usuário
- Realiza validação sintática dos parâmetros
- Encaminha requisições para a camada de aplicação

### Camada de Aplicação
- Orquestra a execução do sistema
- Executa o `assinador.jar`
- Gerencia o ciclo de vida do `simulador.jar` (start, stop, status)
- Verifica pré-condições (porta, arquivos, ambiente)

### Camada de Infraestrutura
- Executa processos (`java -jar`)
- Gerencia arquivos e portas
- Realiza download de dependências externas

---