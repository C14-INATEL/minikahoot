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

- `Servidor`: inicia o servidor TCP na porta `12345`, aguarda a conexao de um cliente e delega o atendimento para `ServidorService`.
- `Cliente`: conecta ao servidor em `localhost:12345`, le todas as mensagens do protocolo, exibe pergunta e alternativas, envia a resposta digitada pelo usuario e mostra resultado e pontuacao.
- `Pergunta`: representa uma pergunta do quiz, com enunciado, alternativas e resposta correta. Internamente, a resposta correta usa indice comecando em `0`, mesmo que a exibicao das alternativas para o usuario comece em `1`.
- `BancoDePerguntas`: mantem a colecao de perguntas e carrega perguntas iniciais.
- `GerenciadorDePontos`: calcula a pontuacao, impede pontuacao negativa e permite obter ranking dos jogadores.
- `ServidorService`: concentra o fluxo do jogo, incluindo boas-vindas, envio da pergunta, leitura da resposta, validacao, pontuacao e encerramento.

## Protocolo de comunicacao

O servidor e o cliente usam mensagens em texto simples via socket TCP.

Exemplo de fluxo:

```text
BEM_VINDO|Bem-vindo ao MiniKahoot!
PERGUNTA|Qual estrutura armazena pares chave-valor em Java?
ALT|1|List
ALT|2|Set
ALT|3|Map
ALT|4|Queue
FIM_PERGUNTA
RESPONDA
RESULTADO|ACERTO
PONTOS|1500
FIM
```

Se o cliente enviar uma resposta invalida ou incorreta, o servidor devolve `RESULTADO|ERRO` e mantem a pontuacao em `0`.

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

### Limpar evidencias antigas e regenerar relatorios

```bash
mvn clean test
```

Esse comando remove a pasta `target/` anterior e gera novamente os relatorios atuais em `target/surefire-reports/`.

### Gerar o pacote da aplicacao

```bash
mvn clean package
```

Depois do empacotamento, o artefato `.jar` fica disponivel em `target/`.

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

Fluxo manual esperado:

1. Inicie o servidor.
2. Execute o cliente em outro terminal.
3. Leia a pergunta e as alternativas exibidas.
4. Quando o cliente mostrar `Digite sua resposta:`, informe o numero da alternativa.
5. Verifique o retorno com `RESULTADO`, `PONTOS` e `FIM`.

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
- executar os testes unitarios com `mvn clean test`
- publicar relatorios JUnit
- gerar o pacote JAR com `mvn package`
- arquivar os artefatos produzidos

### Evidencias geradas

Depois da execucao dos testes e do empacotamento, as principais evidencias ficam em:

- `target/surefire-reports/`
- `target/*.jar`

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

## Notificacao por e-mail no Jenkins

O pipeline possui notificacao por e-mail no bloco `post` do `Jenkinsfile`.
Para funcionar, o Jenkins precisa estar com SMTP configurado em:

`Manage Jenkins -> System -> E-mail Notification`

No caso de Gmail, e necessario usar senha de app, nao a senha normal da conta.

## Melhorias futuras

- Medir o tempo real de resposta do jogador no fluxo cliente-servidor.
- Exibir o ranking completo ao final da partida.
- Permitir multiplos jogadores simultaneos.
- Expandir o banco de perguntas.

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
