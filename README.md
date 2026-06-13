# Mini Kahoot

## Descricao

Mini Kahoot e uma aplicacao cliente/servidor em Java que usa sockets TCP para simular uma comunicacao basica inspirada em plataformas de quiz. O projeto foi organizado com Maven, possui testes unitarios com JUnit 5 e inclui automacao de pipeline com Jenkins e Docker.

Nesta etapa do projeto, a documentacao funcional tambem passou a ser guiada por historias de usuario e por guias de apoio produzidos com IA e revisados pela equipe. Esses materiais ajudaram a consolidar o escopo da NP2, orientar a organizacao do grupo e alinhar a documentacao com o fluxo real implementado no codigo.

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
|-- USER_STORIES.md
|-- docker-compose.yml
|-- docs_ia_generated/
|-- pom.xml
|-- ranking_geral.txt
|-- README.md
`-- src
    |-- main
    |   `-- java
    |       `-- br/com/kahoot
    |           |-- BancoDePerguntas.java
    |           |-- Cliente.java
    |           |-- GerenciadorDePontos.java
    |           |-- Pergunta.java
    |           |-- RankingGeral.java
    |           |-- Servidor.java
    |           `-- ServidorService.java
    `-- test
        `-- java
            `-- br/com/kahoot
                |-- BancoDePerguntasTest.java
                |-- GerenciadorDePontosTest.java
                |-- PerguntaTest.java
                |-- RankingGeralTest.java
                `-- ServidorServiceTest.java
```

## Documentacao de apoio do projeto

- `USER_STORIES.md`: concentra as historias de usuario da NP2, com identificacao, contexto, criterios de aceitacao, rastreabilidade, notas tecnicas e definicao de pronto.
- `docs_ia_generated/`: reune guias de apoio usados como base para discussao interna sobre organizacao do grupo, evolucao do codigo, testes, pipeline Jenkins, Docker e preparacao da entrega.

Os guias em `docs_ia_generated/` nao foram incorporados automaticamente ao projeto. A equipe analisou as sugestoes, selecionou apenas as ideias coerentes com os requisitos e adaptou o conteudo ao estado real da implementacao.

## Historias de usuario implementadas

O arquivo `USER_STORIES.md` registra a evolucao funcional do Mini Kahoot a partir de cinco historias principais:

- `US-001`: conexao do cliente ao servidor TCP local.
- `US-002`: envio de pergunta e alternativas ao jogador.
- `US-003`: envio e validacao da resposta do jogador.
- `US-004`: calculo de pontuacao e ranking dos jogadores.
- `US-005`: pipeline Jenkins em Docker com testes, relatorios e artefatos.

No estado atual do projeto, as historias `US-001`, `US-002`, `US-003` e `US-005` estao concluidas. A `US-004` avancou com a exibicao do ranking no final da partida e com a persistencia da melhor pontuacao por jogador, mas a medicao do tempo real de resposta e o suporte a multiplos jogadores simultaneos seguem como melhorias futuras.

## Componentes principais

- `Servidor`: inicia o servidor TCP na porta `12345`, aguarda a conexao de um cliente e delega o atendimento para `ServidorService`.
- `Cliente`: conecta ao servidor em `localhost:12345`, le todas as mensagens do protocolo, exibe perguntas e alternativas em formato amigavel, envia o nome do jogador e a resposta digitada pelo usuario e mostra resultado, pontuacao e ranking.
- `Pergunta`: representa uma pergunta do quiz, com enunciado, alternativas e resposta correta. Internamente, a resposta correta usa indice comecando em `0`, mesmo que a exibicao das alternativas para o usuario comece em `1`.
- `BancoDePerguntas`: mantem a colecao de perguntas, carrega 30 perguntas base e sorteia 5 delas para cada partida.
- `GerenciadorDePontos`: calcula a pontuacao, impede pontuacao negativa e permite obter ranking dos jogadores.
- `RankingGeral`: persiste em arquivo a melhor pontuacao ja obtida por cada jogador e devolve o ranking ordenado.
- `ServidorService`: concentra o fluxo do jogo, incluindo boas-vindas, coleta do nome do jogador, envio da pergunta, leitura da resposta, validacao, pontuacao, ranking final e encerramento.

## Protocolo de comunicacao

O servidor e o cliente usam mensagens em texto simples via socket TCP.

Exemplo de fluxo:

```text
BEM_VINDO|Bem-vindo ao MiniKahoot!
NOME
PERGUNTA|Qual estrutura armazena pares chave-valor em Java?
ALT|1|List
ALT|2|Set
ALT|3|Map
ALT|4|Queue
FIM_PERGUNTA
RESPONDA
RESULTADO|ACERTO
PONTOS|1500
RANKING_INICIO
RANKING|1|Samuel|7500
RANKING_FIM
FIM
```

O servidor primeiro solicita o nome do jogador com `NOME`, depois repete o bloco da pergunta ao longo de 5 perguntas sorteadas aleatoriamente entre as 30 carregadas no banco. Nao ha repeticao de pergunta dentro da mesma partida. Se o cliente enviar uma resposta invalida ou incorreta, o servidor devolve `RESULTADO|ERRO` e mantem a pontuacao acumulada ate aquele momento. Ao final, o ranking geral e enviado ao cliente e tambem exibido no terminal do servidor.

No terminal do cliente, essas mensagens sao apresentadas de forma mais amigavel. Por exemplo, as alternativas aparecem como `1) List`, `2) Set`, `3) Map`, `4) Queue`, e o prompt de resposta e exibido como `Digite sua resposta (ex: 2):`.

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

Observacao: como o `exec-maven-plugin` esta configurado com `br.com.kahoot.Servidor` como classe principal padrao, a forma mais confiavel de executar cliente e servidor separadamente e usar os comandos `java -cp target/classes ...` apos a compilacao.

### Executar o servidor

Depois de compilar:

```bash
java -cp target/classes br.com.kahoot.Servidor
```

Ou, se quiser usar a configuracao padrao do Maven:

```bash
mvn exec:java -Dexec.mainClass=br.com.kahoot.Servidor
```

### Executar o cliente

Em outro terminal:

```bash
java -cp target/classes br.com.kahoot.Cliente
```

Fluxo manual esperado:

1. Inicie o servidor.
2. Execute o cliente em outro terminal.
3. Quando o cliente mostrar `Digite seu nome:`, informe o nome do jogador.
4. Leia a pergunta e as alternativas exibidas.
5. Quando o cliente mostrar `Digite sua resposta (ex: 2):`, informe o numero da alternativa para cada uma das 5 perguntas sorteadas da sessao.
6. Verifique o retorno com `RESULTADO` e `PONTOS` ao final de cada pergunta.
7. Ao final da quinta pergunta, confira o ranking geral enviado ao cliente e o encerramento com `FIM`.

### Ranking persistido

O ranking geral e salvo no arquivo `ranking_geral.txt`, criado automaticamente na raiz do projeto durante a execucao. Esse arquivo registra a melhor pontuacao obtida por cada jogador entre diferentes execucoes locais do sistema.

### Banco de perguntas

O projeto possui 30 perguntas cadastradas no `BancoDePerguntas`. A cada nova partida, o servidor sorteia 5 perguntas sem repeticao para compor a sessao atual.

## Testes

Os testes ficam em `src/test/java/br/com/kahoot` e cobrem as classes principais do dominio e do servico:

- `PerguntaTest`
- `BancoDePerguntasTest`
- `GerenciadorDePontosTest`
- `RankingGeralTest`
- `ServidorServiceTest`

## Integracao continua com Jenkins

O projeto possui um `Jenkinsfile` com pipeline para:

- validar o ambiente com Java, Maven e Git
- compilar o projeto com Maven
- executar os testes unitarios com `mvn clean test`
- publicar relatorios JUnit
- gerar o pacote JAR com `mvn package`
- arquivar os artefatos produzidos
- enviar notificacoes por e-mail ao final da execucao, quando o SMTP estiver configurado no Jenkins

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
- Permitir multiplos jogadores simultaneos.
- Expandir ainda mais o banco de perguntas com novas categorias ou niveis de dificuldade.

## Entrega limpa

Para validar a entrega final, recomenda-se executar:

```bash
mvn clean test
mvn clean package
```

Resultado esperado:

- testes executados sem falhas
- relatorios atualizados em `target/surefire-reports/`
- arquivo `.jar` gerado em `target/`

Para gerar um arquivo compactado limpo no PowerShell:

```powershell
Compress-Archive -Path Dockerfile.jenkins,Jenkinsfile,README.md,docker-compose.yml,pom.xml,src,.gitignore -DestinationPath minikahoot_entrega.zip -Force
```

O arquivo de entrega nao deve incluir `target/`, `.git/`, arquivos `.zip` internos nem a pasta `.github/modernize/`.

## Uso de IA

Este projeto utilizou apoio de IA como suporte tecnico para:

- estruturacao e refinamento das historias de usuario
- organizacao das etapas de trabalho da equipe
- revisao e melhoria da documentacao
- sugestoes de refatoracao
- ampliacao de testes automatizados
- apoio na organizacao da pipeline e do ambiente Jenkins com Docker
- levantamento de melhorias, correcoes pendentes e criterios de entrega

Os guias gerados com IA ficaram registrados na pasta `docs_ia_generated/` como material de apoio ao projeto. A equipe comparou as respostas obtidas, selecionou as recomendacoes mais aderentes ao Mini Kahoot e aproveitou apenas o que fazia sentido para a implementacao e para os requisitos da disciplina.

Todo o conteudo gerado com apoio de IA foi revisado e adaptado pela equipe antes de ser incorporado ao projeto.

## Autores

- Luis Henrique de Souza Cortes Moreira - GES 642
- Rafael Mello Barbosa da Silva - GES 609
- Gabriel Bissacot Fraguas - GEC 1363
- Samuel Almeida Ralise - GEC 1993
