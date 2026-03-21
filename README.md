# Mini Kahoot - Aplicação Cliente/Servidor em Java

## Descrição

Este projeto consiste na implementação de uma aplicação cliente/servidor em Java que simula um sistema básico de comunicação inspirado em plataformas de quiz como o Kahoot. A aplicação utiliza sockets TCP para estabelecer a comunicação entre cliente e servidor.

O servidor é responsável por inicializar a conexão, aguardar a entrada de um cliente e enviar uma mensagem inicial. O cliente, por sua vez, conecta-se ao servidor e exibe a mensagem recebida.

---

## Objetivo

Desenvolver uma aplicação simples para demonstrar:

* Comunicação entre processos via sockets TCP
* Implementação de cliente e servidor em Java
* Troca de mensagens entre aplicações distribuídas

---

## Tecnologias Utilizadas

* Java
* Sockets TCP (java.net)
* Streams de entrada e saída

---

## Estrutura do Projeto

```text
src/
└── br/
    └── com/
        └── kahoot/
            ├── Servidor.java
            └── Cliente.java
```

---

## Funcionamento

### Servidor

* Inicia na porta 12345
* Aguarda a conexão de um cliente
* Envia uma mensagem de boas-vindas
* Encerra a conexão após o envio

### Cliente

* Conecta ao servidor via localhost na porta 12345
* Recebe a mensagem enviada pelo servidor
* Exibe a mensagem no terminal
* Encerra a conexão

---

## Como Executar

### 1. Compilar os arquivos

```bash
javac br/com/kahoot/*.java
```

### 2. Executar o servidor

```bash
java br.com.kahoot.Servidor
```

### 3. Executar o cliente

Em outro terminal:

```bash
java br.com.kahoot.Cliente
```

---

## Observações

* O servidor deve ser executado antes do cliente.
* A aplicação utiliza comunicação local (localhost).
* O projeto pode ser expandido para incluir lógica de perguntas e respostas.

---

## Autores

Luís Henrique de Souza Côrtes Moreira - GES 642
Rafael Mello Barbosa da Silva - GES 609
Gabriel Bissacot Fraguas - GEC 1363
Samuel Almeida Ralise - GEC 1993
