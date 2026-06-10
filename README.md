# Mini Kahoot

## Descricao

Mini Kahoot e uma aplicacao cliente/servidor em Java que usa sockets TCP para simular uma comunicacao basica inspirada em plataformas de quiz. O projeto foi organizado com Maven, possui testes unitarios com JUnit 5 e inclui automacao de pipeline com Jenkins e Docker.

## Tecnologias utilizadas

- Java 17
- Maven
- Sockets TCP (`java.net`)
- JUnit 5
- Mockito
- Jenkins
- Docker
- Docker Compose

## Estrutura do projeto

```text
.
|-- Dockerfile.jenkins
|-- Jenkinsfile
|-- docker-compose.yml
|-- pom.xml
|-- README.md
`-- src
    |-- main
    |   `-- java
    |       `-- br/com/kahoot
    |           |-- BancoDePerguntas.java
    |           |-- Cliente.java
    |           |-- GerenciadorDePontos.java
    |           |-- Pergunta.java
    |           |-- Servidor.java
    |           `-- ServidorService.java
    `-- test
        `-- java
            `-- br/com/kahoot
                |-- BancoDePerguntasTest.java
                |-- GerenciadorPontosTest.java
                |-- PerguntaTest.java
                `-- ServidorServiceTest.java
```

## Componentes principais

- `Servidor`: inicia o servidor TCP na porta `12345`, aguarda conexao de um cliente e envia a mensagem inicial.
- `Cliente`: conecta ao servidor em `localhost:12345` e exibe a mensagem recebida.
- `Pergunta`: representa uma pergunta do quiz, com enunciado, alternativas e resposta correta.
- `BancoDePerguntas`: mantem a colecao de perguntas e carrega perguntas iniciais.
- `GerenciadorDePontos`: controla a pontuacao dos jogadores com base no tempo de resposta.
- `ServidorService`: concentra a logica de envio da mensagem de boas-vindas via socket.

## Requisitos

- Java 17 instalado
- Maven 3.x instalado

## Como executar

### Compilar o projeto

```bash
mvn clean compile
```

### Executar os testes unitarios

```bash
mvn test
```

### Gerar o pacote da aplicacao

```bash
mvn clean package
```

### Executar o servidor

```bash
mvn exec:java -Dexec.mainClass=br.com.kahoot.Servidor
```

### Executar o cliente

Em outro terminal:

```bash
mvn exec:java -Dexec.mainClass=br.com.kahoot.Cliente
```

## Testes

Os testes ficam em `src/test/java/br/com/kahoot` e cobrem as classes principais do dominio e do servico:

- `PerguntaTest`
- `BancoDePerguntasTest`
- `GerenciadorPontosTest`
- `ServidorServiceTest`

## Integracao continua com Jenkins

O projeto possui um `Jenkinsfile` com pipeline para:

- validar o ambiente com Java, Maven e Git
- compilar o projeto com Maven
- executar os testes unitarios
- publicar relatorios JUnit
- gerar o pacote JAR
- arquivar os artefatos produzidos

## Ambiente Jenkins com Docker

### `Dockerfile.jenkins`

Cria uma imagem baseada em `jenkins/jenkins:lts-jdk17` com:

- Maven
- Git
- Docker CLI

### `docker-compose.yml`

Sobe um servico Jenkins local com:

- porta `8080` para a interface web
- porta `50000` para agentes Jenkins
- volume persistente `jenkins_home`

### Subir o Jenkins localmente

```bash
docker compose up -d
```

Depois disso, acesse `http://localhost:8080`.

## Autores

- Luis Henrique de Souza Cortes Moreira - GES 642
- Rafael Mello Barbosa da Silva - GES 609
- Gabriel Bissacot Fraguas - GEC 1363
- Samuel Almeida Ralise - GEC 1993
