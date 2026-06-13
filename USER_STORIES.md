# Historias de Usuario - MiniKahoot NP2

## US-1 - Conexao do Cliente ao Servidor TCP

### Identificacao

| Campo | Valor |
| --- | --- |
| ID | US-001 |
| Epico | Comunicacao Cliente-Servidor |
| Prioridade | Alta |
| Status Final | Concluido |
| Rastreabilidade | PR #1 · PR #2 · PR #8 |

### Historia de Usuario

Como jogador utilizando o cliente do MiniKahoot,  
eu quero me conectar ao servidor TCP local,  
para que eu possa iniciar uma partida de quiz e receber as mensagens enviadas pelo servidor.

### Contexto / Motivacao

Antes desta historia, o projeto precisava garantir a base minima de comunicacao entre cliente e servidor. Essa entrega estabeleceu o alicerce para o envio de perguntas, recebimento de respostas, calculo de pontuacao e encerramento da partida.

### Criterios de Aceitacao

#### AC-01 - Inicializacao do servidor

Dado que o projeto foi compilado corretamente  
E o servidor e executado pela classe `Servidor`  
Quando o servidor e iniciado  
Entao ele deve abrir uma conexao TCP na porta `12345`  
E deve aguardar a conexao de um cliente.

#### AC-02 - Conexao do cliente

Dado que o servidor esta em execucao na porta `12345`  
Quando o jogador executa a classe `Cliente`  
Entao o cliente deve se conectar ao servidor em `localhost:12345`  
E a conexao deve permanecer ativa ate o recebimento da mensagem `FIM`.

#### AC-03 - Recebimento da mensagem inicial

Dado que o cliente esta conectado ao servidor  
Quando o servidor inicia o atendimento do cliente  
Entao o cliente deve receber a mensagem `BEM_VINDO|Bem-vindo ao MiniKahoot!`  
E deve exibir essa mensagem no terminal.

#### AC-04 - Encerramento controlado da comunicacao

Dado que o servidor finalizou o fluxo da partida  
Quando o cliente recebe a mensagem `FIM`  
Entao o cliente deve encerrar a leitura das mensagens  
E a conexao deve ser fechada sem erro inesperado.

### Rastreabilidade

| Artefato | Referencia |
| --- | --- |
| PR de implementacao | PR #1 - Atualizacoes iniciais no Backend |
| PR de documentacao/build | PR #2 - Backend README e `pom.xml` |
| PR de consolidacao | PR #8 - Backend |
| Codigo impactado | `Servidor.java` · `Cliente.java` · `ServidorService.java` |
| Testes automatizados | `ServidorServiceTest` |
| Protocolo impactado | `BEM_VINDO` · `FIM` |

### Notas Tecnicas

A comunicacao e feita com sockets TCP usando `java.net.Socket` no cliente e `java.net.ServerSocket` no servidor. O servidor delega a regra principal de atendimento para `ServidorService`.

### Definicao de Pronto

- [x] Servidor abre conexao TCP na porta `12345`.
- [x] Cliente conecta em `localhost:12345`.
- [x] Mensagem inicial e enviada pelo servidor.
- [x] Cliente le mensagens ate `FIM`.
- [x] Fluxo basico validado por teste unitario.
- [x] Codigo versionado no GitHub por PR/commit.

---

## US-2 - Envio de Pergunta e Alternativas ao Jogador

### Identificacao

| Campo | Valor |
| --- | --- |
| ID | US-002 |
| Epico | Fluxo de Quiz |
| Prioridade | Alta |
| Status Final | Concluido |
| Rastreabilidade | PR #4 · PR #6 · PR #8 |

### Historia de Usuario

Como jogador conectado ao servidor,  
eu quero receber uma pergunta com alternativas numeradas,  
para que eu consiga escolher uma resposta durante a partida.

### Contexto / Motivacao

Para transformar a conexao TCP basica em um quiz jogavel, foi necessario modelar perguntas e integrar esse dominio ao servidor. A classe `Pergunta` representa o enunciado, as alternativas e a resposta correta. O `BancoDePerguntas` passou a carregar cinco perguntas iniciais.

### Criterios de Aceitacao

#### AC-01 - Existencia de perguntas iniciais

Dado que o sistema inicializa o banco de perguntas  
Quando a classe `BancoDePerguntas` e instanciada  
Entao deve haver perguntas iniciais disponiveis  
E cada pergunta deve possuir enunciado, alternativas e resposta correta.

#### AC-02 - Validacao da pergunta

Dado que uma nova pergunta sera criada  
Quando o enunciado, as alternativas ou a resposta correta forem invalidos  
Entao o sistema deve rejeitar a criacao da pergunta  
E deve impedir estados invalidos no dominio.

#### AC-03 - Envio do enunciado ao cliente

Dado que o cliente esta conectado ao servidor  
Quando o servidor inicia o fluxo do quiz  
Entao o servidor deve enviar uma mensagem no formato `PERGUNTA|<enunciado>`  
E o cliente deve exibir essa pergunta no terminal.

#### AC-04 - Envio das alternativas

Dado que uma pergunta possui alternativas cadastradas  
Quando o servidor envia a pergunta ao cliente  
Entao cada alternativa deve ser enviada no formato `ALT|<numero>|<texto>`  
E a numeracao exibida ao jogador deve comecar em `1`.

#### AC-05 - Finalizacao do bloco da pergunta

Dado que todas as alternativas foram enviadas  
Quando o servidor termina o envio da pergunta  
Entao ele deve enviar a mensagem `FIM_PERGUNTA`  
E em seguida deve solicitar a resposta com `RESPONDA`.

### Rastreabilidade

| Artefato | Referencia |
| --- | --- |
| PR de implementacao | PR #4 - Adiciona perguntas |
| PR de testes | PR #6 - Testes unitarios adicionais para `ServidorService` |
| PR de consolidacao | PR #8 - Backend |
| Codigo impactado | `Pergunta.java` · `BancoDePerguntas.java` · `ServidorService.java` |
| Testes automatizados | `PerguntaTest` · `BancoDePerguntasTest` · `ServidorServiceTest` |
| Protocolo impactado | `PERGUNTA` · `ALT` · `FIM_PERGUNTA` · `RESPONDA` |

### Notas Tecnicas

Internamente, a resposta correta usa indice iniciado em `0`, enquanto a exibicao para o usuario usa alternativas numeradas a partir de `1`. Por isso, o servidor converte a resposta enviada pelo cliente antes de validar.

### Definicao de Pronto

- [x] Classe `Pergunta` implementada com validacoes.
- [x] Classe `BancoDePerguntas` criada com perguntas iniciais.
- [x] Servidor envia enunciado e alternativas pelo protocolo.
- [x] Cliente exibe as mensagens recebidas.
- [x] Testes unitarios cobrem pergunta, banco e protocolo.
- [x] Implementacao revisada e integrada a branch `Backend`.

---

## US-3 - Envio e Validacao da Resposta do Jogador

### Identificacao

| Campo | Valor |
| --- | --- |
| ID | US-003 |
| Epico | Validacao da Partida |
| Prioridade | Alta |
| Status Final | Concluido |
| Rastreabilidade | PR #6 · PR #7 · PR #8 |

### Historia de Usuario

Como jogador participando de uma partida,  
eu quero enviar minha resposta ao servidor,  
para que o sistema valide se eu acertei ou errei a pergunta.

### Contexto / Motivacao

Apos o envio das perguntas, o jogo precisava permitir interacao real do jogador. O cliente identifica `RESPONDA`, solicita uma entrada no terminal com o prompt `Digite sua resposta (ex: 2):` e envia o valor ao servidor. O servidor trata entradas invalidas e compara a resposta com a alternativa correta.

### Criterios de Aceitacao

#### AC-01 - Solicitacao da resposta

Dado que o servidor enviou a pergunta e as alternativas  
Quando o bloco da pergunta e finalizado  
Entao o servidor deve enviar a mensagem `RESPONDA`  
E o cliente deve solicitar que o jogador digite sua resposta.

#### AC-02 - Envio da resposta pelo cliente

Dado que o cliente recebeu a mensagem `RESPONDA`  
Quando o jogador digita uma alternativa no terminal  
Entao o cliente deve enviar essa resposta ao servidor  
E a resposta deve ser transmitida como texto pelo socket.

#### AC-03 - Validacao de resposta correta

Dado que a pergunta possui uma alternativa correta cadastrada  
E o jogador informou a alternativa correta  
Quando o servidor processa a resposta  
Entao o servidor deve retornar `RESULTADO|ACERTO`  
E a pontuacao do jogador deve ser atualizada.

#### AC-04 - Validacao de resposta incorreta

Dado que a pergunta possui uma alternativa correta cadastrada  
E o jogador informou uma alternativa incorreta  
Quando o servidor processa a resposta  
Entao o servidor deve retornar `RESULTADO|ERRO`  
E a pontuacao nao deve ser aumentada.

#### AC-05 - Tratamento de resposta invalida

Dado que o jogador informou uma resposta nao numerica ou invalida  
Quando o servidor tenta interpretar a resposta  
Entao o sistema nao deve quebrar a execucao  
E deve retornar `RESULTADO|ERRO`.

### Rastreabilidade

| Artefato | Referencia |
| --- | --- |
| PR de testes do servidor | PR #6 - Testes unitarios adicionais para `ServidorService` |
| PR de cenario adicional | PR #7 - Teste simulando conexao com dois clientes |
| PR de consolidacao | PR #8 - Backend |
| Codigo impactado | `Cliente.java` · `ServidorService.java` · `Pergunta.java` |
| Testes automatizados | `ServidorServiceTest` · `PerguntaTest` |
| Protocolo impactado | `RESPONDA` · `RESULTADO|ACERTO` · `RESULTADO|ERRO` |

### Notas Tecnicas

O cliente continua lendo mensagens do servidor ate receber `FIM`. Quando recebe `RESPONDA`, ele pausa a leitura, solicita a entrada do jogador e envia a resposta pelo socket. O servidor trata a resposta recebida e evita que entradas invalidas interrompam a execucao.

### Definicao de Pronto

- [x] Cliente identifica a mensagem `RESPONDA`.
- [x] Cliente envia a resposta digitada pelo jogador.
- [x] Servidor le a resposta enviada.
- [x] Servidor valida acerto e erro.
- [x] Respostas invalidas sao tratadas sem quebrar o fluxo.
- [x] Testes unitarios cobrem cenarios de acerto, erro e entrada invalida.

---

## US-4 - Calculo de Pontuacao e Ranking dos Jogadores

### Identificacao

| Campo | Valor |
| --- | --- |
| ID | US-004 |
| Epico | Pontuacao e Ranking |
| Prioridade | Alta |
| Status Final | Parcialmente Concluido |
| Rastreabilidade | PR #5 · PR #8 |

### Historia de Usuario

Como jogador do MiniKahoot,  
eu quero receber pontos ao acertar uma pergunta e ter minha pontuacao organizada em um ranking,  
para que meu desempenho na partida seja registrado e comparado com outros jogadores.

### Contexto / Motivacao

A pontuacao e uma parte central de qualquer jogo de quiz. No MiniKahoot, `GerenciadorDePontos` calcula a pontuacao, impede valores negativos e organiza o ranking em memoria. O servidor integra `PONTOS|...` ao fluxo da partida. Alem disso, o ranking passou a ser exibido ao final da partida e a melhor pontuacao de cada jogador passou a ser persistida em arquivo por meio de `RankingGeral`.

### Criterios de Aceitacao

#### AC-01 - Pontuacao inicial do jogador

Dado que um jogador foi cadastrado no gerenciador de pontos  
Quando a partida ainda nao teve acertos  
Entao a pontuacao inicial do jogador deve ser `0`.

#### AC-02 - Pontuacao ao acertar resposta

Dado que o jogador respondeu corretamente  
Quando o servidor processa a resposta  
Entao o sistema deve adicionar pontos ao jogador  
E o servidor deve retornar uma mensagem `PONTOS|<valor>`.

#### AC-03 - Ausencia de pontuacao ao errar

Dado que o jogador respondeu incorretamente  
Quando o servidor processa a resposta  
Entao o sistema nao deve adicionar pontos  
E a pontuacao retornada deve permanecer no valor acumulado ate aquele momento.

#### AC-04 - Bloqueio de pontuacao negativa

Dado que o calculo de pontos considera tempo de resposta  
Quando o tempo informado ultrapassa o limite da regra  
Entao a pontuacao calculada nao deve ficar negativa  
E o valor minimo deve ser `0`.

#### AC-05 - Ordenacao do ranking

Dado que existem jogadores com pontuacoes diferentes  
Quando o sistema solicita o ranking no dominio  
Entao os jogadores devem ser retornados ordenados da maior para a menor pontuacao.

#### AC-06 - Exibicao do ranking ao final da partida

Dado que a partida terminou  
Quando o servidor finaliza o fluxo do quiz  
Entao o cliente deve receber o ranking geral no protocolo final  
E o ranking deve listar os jogadores ordenados da maior para a menor pontuacao registrada.

#### AC-07 - Persistencia da melhor pontuacao

Dado que um jogador concluiu a partida  
Quando o servidor registra a pontuacao final  
Entao a melhor pontuacao daquele jogador deve ser salva em arquivo  
E esse valor deve poder ser reutilizado em execucoes futuras do sistema.

### Rastreabilidade

| Artefato | Referencia |
| --- | --- |
| PR de implementacao | PR #5 - Esqueleto do gerenciador de pontos |
| PR de consolidacao | PR #8 - Backend |
| Commits relacionados | `feat: implementa ranking de jogadores por pontuacao` · `test: adiciona testes de ranking no gerenciador de pontos` · `feat: integra pontuacao ao fluxo do servidor` |
| Codigo impactado | `GerenciadorDePontos.java` · `RankingGeral.java` · `ServidorService.java` |
| Testes automatizados | `GerenciadorDePontosTest` · `RankingGeralTest` · `ServidorServiceTest` |
| Protocolo impactado | `PONTOS` · `RANKING_INICIO` · `RANKING` · `RANKING_FIM` |

### Notas Tecnicas

A regra de pontuacao considera uma formula baseada em tempo e aplica protecao para impedir pontuacao negativa. No fluxo atual do servidor, a pontuacao integrada usa tempo fixo, gerando pontuacao maxima quando o jogador acerta. O ranking final passou a ser enviado ao cliente e a melhor pontuacao de cada jogador passou a ser persistida em arquivo. A medicao real do tempo de resposta continua como melhoria futura.

### Definicao de Pronto

- [x] Gerenciador de pontos implementado.
- [x] Pontuacao inicial definida como `0`.
- [x] Acertos adicionam pontos.
- [x] Erros nao adicionam pontos.
- [x] Pontuacao negativa e impedida.
- [x] Ranking ordenado existe no dominio.
- [x] Ranking geral e exibido ao cliente ao final da partida.
- [x] Melhor pontuacao do jogador e persistida em arquivo.
- [x] Testes unitarios cobrem pontuacao e ranking.
- [ ] Tempo real de resposta ainda precisa ser medido no fluxo cliente-servidor.

---

## US-5 - Pipeline Jenkins em Docker com Testes, Relatorios e Artefatos

### Identificacao

| Campo | Valor |
| --- | --- |
| ID | US-005 |
| Epico | CI/CD e Evidencias de Qualidade |
| Prioridade | Alta |
| Status Final | Concluido |
| Rastreabilidade | PR #2 · PR #8 |

### Historia de Usuario

Como integrante do grupo de desenvolvimento,  
eu quero que o projeto seja validado automaticamente por um pipeline Jenkins em container Docker,  
para que build, testes, relatorios e artefatos sejam gerados de forma reproduzivel antes da entrega.

### Contexto / Motivacao

A NP2 exige CI/CD sem GitHub Actions, alem de testes automatizados e relatorios entregues via pipeline. Para atender a essa exigencia, o projeto adotou Jenkins em container Docker. O `Dockerfile.jenkins` prepara o ambiente com Jenkins, Java, Maven, Git e Docker CLI. O `docker-compose.yml` permite subir o Jenkins localmente. O `Jenkinsfile` organiza os estagios de validacao do ambiente, build Maven, testes unitarios, empacotamento, publicacao de relatorios JUnit, arquivamento de artefatos e notificacao por e-mail quando o SMTP estiver configurado.

### Criterios de Aceitacao

#### AC-01 - Jenkins executando em container

Dado que o Docker esta instalado no ambiente local  
Quando o comando `docker compose up -d --build` e executado  
Entao o Jenkins deve subir em container  
E a interface deve ficar acessivel em `http://localhost:8080`.

#### AC-02 - Validacao do ambiente

Dado que o pipeline Jenkins foi iniciado  
Quando o stage de ambiente e executado  
Entao o Jenkins deve validar a disponibilidade de Java, Maven e Git.

#### AC-03 - Build Maven

Dado que o codigo esta disponivel na branch configurada  
Quando o stage de build e executado  
Entao o Jenkins deve compilar o projeto com Maven  
E a compilacao deve terminar sem erro.

#### AC-04 - Execucao dos testes unitarios

Dado que existem testes em `src/test/java/br/com/kahoot`  
Quando o stage de testes e executado  
Entao o Jenkins deve executar os testes unitarios  
E deve publicar os relatorios JUnit gerados em `target/surefire-reports`.

#### AC-05 - Geracao de artefato

Dado que o build e os testes foram concluidos com sucesso  
Quando o stage de package e executado  
Entao o Maven deve gerar o arquivo `.jar` em `target/`  
E o Jenkins deve arquivar esse artefato como evidencia.

#### AC-06 - Notificacao por e-mail

Dado que o Jenkins possui SMTP configurado  
Quando o pipeline finaliza com sucesso ou falha  
Entao o Jenkins deve enviar uma notificacao por e-mail  
E a ausencia de SMTP deve ser documentada como limitacao de ambiente, nao como falha da regra de negocio.

### Rastreabilidade

| Artefato | Referencia |
| --- | --- |
| PR de configuracao inicial | PR #2 - Backend README e `pom.xml` |
| PR de consolidacao | PR #8 - Backend |
| Commits relacionados | `ci: adiciona Dockerfile do Jenkins` · `ci: adiciona docker compose para Jenkins` · `ci: adiciona stage de build Maven` · `ci: adiciona stage de testes unitarios` · `adicionado stage de package jar` · `ci: ajusta pipeline para testes e artefatos do maven` |
| Codigo/configuracao impactada | `Jenkinsfile` · `Dockerfile.jenkins` · `docker-compose.yml` · `pom.xml` |
| Testes automatizados | `PerguntaTest` · `BancoDePerguntasTest` · `GerenciadorDePontosTest` · `ServidorServiceTest` |
| Evidencias geradas | Relatorios JUnit · Artefato `.jar` · Console Output do Jenkins |

### Notas Tecnicas

O projeto nao utiliza GitHub Actions, respeitando a restricao da NP2. A execucao do Jenkins depende do ambiente local com Docker. A notificacao por e-mail depende de configuracao SMTP manual no Jenkins, especialmente quando for usado Gmail com senha de app.

### Definicao de Pronto

- [x] `Dockerfile.jenkins` criado.
- [x] `docker-compose.yml` criado.
- [x] `Jenkinsfile` criado.
- [x] Pipeline compila o projeto.
- [x] Pipeline executa testes unitarios.
- [x] Pipeline publica relatorios JUnit.
- [x] Pipeline gera e arquiva artefato `.jar`.
- [x] README documenta execucao local e Jenkins.
- [x] SMTP deve ser configurado no ambiente Jenkins caso a equipe demonstre notificacao por e-mail.
