# Runner
Projeto para a disciplina de Implementação e Integração de Software

---

## Integrantes
| Curso                   | Nome                 | Matrícula    |
|-------------------------|--------------------|-------------|
| Engenharia de Software  | Hugo Pereira Borges | 202403075   |

---

## 1. Visão Geral

O Sistema Runner tem como objetivo facilitar a execução de aplicações Java por meio de linha de comando, abstraindo do usuário detalhes relacionados à configuração de ambiente e à execução manual de arquivos `.jar`.

---

## 2. Entreáveis
- assinador.jar
- CLI para o assinador
- CLI para o simulador (hubsaude)
---

## Plano de Trabalho – Sistema Runner

## Fase 0 – Preparação e Estruturação do Projeto
Objetivo: Organizar o ambiente de desenvolvimento e estrutura base do sistema.

## Fase 1 – Desenvolvimento do Assinador.jar
Objetivo: Criar a aplicação Java responsável pela simulação de assinaturas digitais e validação de parâmetros.

- Criação do projeto Java (esqueleto, pom.xml, estrutura de pacotes).  
- Implementação da interface `SignatureService` com simulação de métodos `sign` e `validate`.  
- Validação rigorosa de parâmetros de entrada.  
- Implementação do modo servidor via HTTP (`/sign` e `/validate`).  
- Preparação para integração com CLIs e suporte a dispositivos criptográficos simulados (PKCS#11).  

---

## Fase 2 – Desenvolvimento do CLI do Assinador
Objetivo: Criar o CLI que gerencia o `assinador.jar` local e via servidor, de forma multiplataforma.

- Implementação do CLI em Go (cross-compiling para Windows, Linux e macOS).  
- Detecção de portas livres e reutilização de instâncias em execução.  
- Invocação do `assinador.jar` no modo local ou via HTTP.  
- Registro de runtime, porta e PID no banco de dados local.  

---

## Fase 3 – Desenvolvimento do CLI do Simulador
Objetivo: Criar o CLI que gerencia o simulador HubSaúde (`simulador.jar`) de forma multiplataforma.

- Gerenciamento do download e versão do simulador.jar.  
- Controle do ciclo de vida (start, stop, status).  
- Verificação de portas disponíveis antes da inicialização.  
- Integração com banco de dados local para registrar estado e PID.  

---

## Fase 4 – Integração
Objetivo: Garantir comunicação confiável entre os CLIs e as aplicações Java.

- Integração do CLI do assinador com o `assinador.jar` (modo local e HTTP).  
- Integração do CLI do simulador com o `simulador.jar`.  
- Fluxos de execução para criação e validação de assinaturas simuladas.  
- Tratamento de erros e mensagens de feedback unificadas.  

---
