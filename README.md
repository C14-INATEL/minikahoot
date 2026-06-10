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
                |-- GerenciadorDePontosTest.java
                |-- PerguntaTest.java
                `-- ServidorServiceTest.java
```

## Componentes principais

- `Servidor`: inicia o servidor TCP na porta `12345`, aguarda conexao de um cliente e envia a mensagem inicial.
- `Cliente`: conecta ao servidor em `localhost:12345` e exibe a mensagem recebida.
- `Pergunta`: representa uma pergunta do quiz, com enunciado, alternativas e resposta correta. Internamente, a resposta correta usa indice comecando em `0`, mesmo que a exibicao das alternativas para o usuario comece em `1`.
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

### Executar com Maven

Para usar o `exec-maven-plugin` configurado no projeto:

```bash
mvn exec:java
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

Ou, depois de compilar:

```bash
java -cp target/classes br.com.kahoot.Cliente
```

## Testes

Os testes ficam em `src/test/java/br/com/kahoot` e cobrem as classes principais do dominio e do servico:

- `PerguntaTest`
- `BancoDePerguntasTest`
- `GerenciadorDePontosTest`
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
docker compose up -d --build
```

Depois disso, acesse `http://localhost:8080`.

### Observacoes sobre o Jenkins

- O envio de e-mails definido no `Jenkinsfile` depende de SMTP configurado no Jenkins.
- Sem essa configuracao, o pipeline pode executar normalmente, mas as notificacoes por e-mail nao serao enviadas.

## Uso de IA

Este projeto utilizou apoio de IA como suporte tecnico para:

- revisao e melhoria da documentacao
- sugestoes de refatoracao
- ampliacao de testes automatizados
- apoio na organizacao da pipeline e do ambiente Jenkins com Docker

Todo o conteudo gerado com apoio de IA foi revisado e adaptado pela equipe antes de ser incorporado ao projeto.

## Autores

- Luis Henrique de Souza Cortes Moreira - GES 642
- Rafael Mello Barbosa da Silva - GES 609
- Gabriel Bissacot Fraguas - GEC 1363
- Samuel Almeida Ralise - GEC 1993
