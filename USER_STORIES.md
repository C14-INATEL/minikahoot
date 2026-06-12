# Histórias de Usuário — MiniKahoot NP2

## US-1 — Conexão do Cliente ao Servidor TCP

### Identificação

| Campo           | Valor                        |
| --------------- | ---------------------------- |
| ID              | US-001                       |
| Épico           | Comunicação Cliente-Servidor |
| Prioridade      | Alta                         |
| Status Final    | Concluído                    |
| Rastreabilidade | PR #1 · PR #2 · PR #8        |

### História de Usuário

Como jogador utilizando o cliente do MiniKahoot,
eu quero me conectar ao servidor TCP local,
para que eu possa iniciar uma partida de quiz e receber as mensagens enviadas pelo servidor.

### Contexto / Motivação

Antes desta história, o projeto precisava garantir a base mínima de comunicação entre cliente e servidor. Como o MiniKahoot é uma aplicação inspirada em quiz, o primeiro passo funcional foi permitir que o cliente se conectasse ao servidor pela porta definida e recebesse uma mensagem inicial. Essa entrega estabelece a base para as demais funcionalidades, como envio de perguntas, resposta do jogador, cálculo de pontuação e encerramento da partida.

### Critérios de Aceitação

#### AC-01 — Inicialização do servidor

Dado que o projeto foi compilado corretamente
E o servidor é executado pela classe `Servidor`
Quando o servidor é iniciado
Então ele deve abrir uma conexão TCP na porta `12345`
E deve aguardar a conexão de um cliente.

#### AC-02 — Conexão do cliente

Dado que o servidor está em execução na porta `12345`
Quando o jogador executa a classe `Cliente`
Então o cliente deve se conectar ao servidor em `localhost:12345`
E a conexão deve permanecer ativa até o recebimento da mensagem `FIM`.

#### AC-03 — Recebimento da mensagem inicial

Dado que o cliente está conectado ao servidor
Quando o servidor inicia o atendimento do cliente
Então o cliente deve receber a mensagem `BEM_VINDO|Bem-vindo ao MiniKahoot!`
E deve exibir essa mensagem no terminal.

#### AC-04 — Encerramento controlado da comunicação

Dado que o servidor finalizou o fluxo da partida
Quando o cliente recebe a mensagem `FIM`
Então o cliente deve encerrar a leitura das mensagens
E a conexão deve ser fechada sem erro inesperado.

### Rastreabilidade

| Artefato                 | Referência                                                |
| ------------------------ | --------------------------------------------------------- |
| PR de implementação      | PR #1 — Atualizações iniciais no Backend                  |
| PR de documentação/build | PR #2 — Backend README e `pom.xml`                        |
| PR de consolidação       | PR #8 — Backend                                           |
| Código impactado         | `Servidor.java` · `Cliente.java` · `ServidorService.java` |
| Testes automatizados     | `ServidorServiceTest`                                     |
| Protocolo impactado      | `BEM_VINDO` · `FIM`                                       |

### Notas Técnicas

A comunicação é feita com sockets TCP usando `java.net.Socket` no cliente e `java.net.ServerSocket` no servidor. O servidor delega a regra principal de atendimento para a classe `ServidorService`, evitando que toda a lógica fique diretamente dentro de `Servidor.java`.

### Definição de Pronto

* [x] Servidor abre conexão TCP na porta `12345`.
* [x] Cliente conecta em `localhost:12345`.
* [x] Mensagem inicial é enviada pelo servidor.
* [x] Cliente lê mensagens até `FIM`.
* [x] Fluxo básico validado por teste unitário.
* [x] Código versionado no GitHub por PR/commit.

---

## US-2 — Envio de Pergunta e Alternativas ao Jogador

### Identificação

| Campo           | Valor                 |
| --------------- | --------------------- |
| ID              | US-002                |
| Épico           | Fluxo de Quiz         |
| Prioridade      | Alta                  |
| Status Final    | Concluído             |
| Rastreabilidade | PR #4 · PR #6 · PR #8 |

### História de Usuário

Como jogador conectado ao servidor,
eu quero receber uma pergunta com alternativas numeradas,
para que eu consiga escolher uma resposta durante a partida.

### Contexto / Motivação

Para que o MiniKahoot deixasse de ser apenas uma conexão TCP básica e passasse a funcionar como um quiz, foi necessário criar uma estrutura de perguntas e integrar esse domínio ao servidor. A classe `Pergunta` representa o enunciado, as alternativas e a resposta correta. A classe `BancoDePerguntas` mantém perguntas iniciais disponíveis para o jogo. O servidor usa essas informações para enviar ao cliente uma pergunta e suas alternativas em um protocolo simples de texto.

### Critérios de Aceitação

#### AC-01 — Existência de perguntas iniciais

Dado que o sistema inicializa o banco de perguntas
Quando a classe `BancoDePerguntas` é instanciada
Então deve haver perguntas iniciais disponíveis
E cada pergunta deve possuir enunciado, alternativas e resposta correta.

#### AC-02 — Validação da pergunta

Dado que uma nova pergunta será criada
Quando o enunciado, as alternativas ou a resposta correta forem inválidos
Então o sistema deve rejeitar a criação da pergunta
E deve impedir estados inválidos no domínio.

#### AC-03 — Envio do enunciado ao cliente

Dado que o cliente está conectado ao servidor
Quando o servidor inicia o fluxo do quiz
Então o servidor deve enviar uma mensagem no formato `PERGUNTA|<enunciado>`
E o cliente deve exibir essa pergunta no terminal.

#### AC-04 — Envio das alternativas

Dado que uma pergunta possui alternativas cadastradas
Quando o servidor envia a pergunta ao cliente
Então cada alternativa deve ser enviada no formato `ALT|<numero>|<texto>`
E a numeração exibida ao jogador deve começar em `1`.

#### AC-05 — Finalização do bloco da pergunta

Dado que todas as alternativas foram enviadas
Quando o servidor termina o envio da pergunta
Então ele deve enviar a mensagem `FIM_PERGUNTA`
E em seguida deve solicitar a resposta com `RESPONDA`.

### Rastreabilidade

| Artefato             | Referência                                                         |
| -------------------- | ------------------------------------------------------------------ |
| PR de implementação  | PR #4 — Adiciona perguntas                                         |
| PR de testes         | PR #6 — Testes unitários adicionais para `ServidorService`         |
| PR de consolidação   | PR #8 — Backend                                                    |
| Código impactado     | `Pergunta.java` · `BancoDePerguntas.java` · `ServidorService.java` |
| Testes automatizados | `PerguntaTest` · `BancoDePerguntasTest` · `ServidorServiceTest`    |
| Protocolo impactado  | `PERGUNTA` · `ALT` · `FIM_PERGUNTA` · `RESPONDA`                   |

### Notas Técnicas

Internamente, a resposta correta usa índice iniciado em `0`, enquanto a exibição para o usuário usa alternativas numeradas a partir de `1`. Por isso, o servidor precisa converter a resposta enviada pelo cliente antes de validar.

### Definição de Pronto

* [x] Classe `Pergunta` implementada com validações.
* [x] Classe `BancoDePerguntas` criada com perguntas iniciais.
* [x] Servidor envia enunciado e alternativas pelo protocolo.
* [x] Cliente exibe as mensagens recebidas.
* [x] Testes unitários cobrem pergunta, banco e protocolo.
* [x] Implementação revisada e integrada à branch `Backend`.

---

## US-3 — Envio e Validação da Resposta do Jogador

### Identificação

| Campo           | Valor                 |
| --------------- | --------------------- |
| ID              | US-003                |
| Épico           | Validação da Partida  |
| Prioridade      | Alta                  |
| Status Final    | Concluído             |
| Rastreabilidade | PR #6 · PR #7 · PR #8 |

### História de Usuário

Como jogador participando de uma partida,
eu quero enviar minha resposta ao servidor,
para que o sistema valide se eu acertei ou errei a pergunta.

### Contexto / Motivação

Após o servidor enviar a pergunta e as alternativas, o jogo precisava permitir interação real do jogador. Para isso, o cliente passou a identificar a mensagem `RESPONDA`, solicitar uma entrada no terminal e enviar essa resposta ao servidor. O servidor, por sua vez, passou a ler a resposta, tratar entradas inválidas e comparar a alternativa escolhida com a resposta correta cadastrada no domínio da pergunta.

### Critérios de Aceitação

#### AC-01 — Solicitação da resposta

Dado que o servidor enviou a pergunta e as alternativas
Quando o bloco da pergunta é finalizado
Então o servidor deve enviar a mensagem `RESPONDA`
E o cliente deve solicitar que o jogador digite sua resposta.

#### AC-02 — Envio da resposta pelo cliente

Dado que o cliente recebeu a mensagem `RESPONDA`
Quando o jogador digita uma alternativa no terminal
Então o cliente deve enviar essa resposta ao servidor
E a resposta deve ser transmitida como texto pelo socket.

#### AC-03 — Validação de resposta correta

Dado que a pergunta possui uma alternativa correta cadastrada
E o jogador informou a alternativa correta
Quando o servidor processa a resposta
Então o servidor deve retornar `RESULTADO|ACERTO`
E a pontuação do jogador deve ser atualizada.

#### AC-04 — Validação de resposta incorreta

Dado que a pergunta possui uma alternativa correta cadastrada
E o jogador informou uma alternativa incorreta
Quando o servidor processa a resposta
Então o servidor deve retornar `RESULTADO|ERRO`
E a pontuação não deve ser aumentada.

#### AC-05 — Tratamento de resposta inválida

Dado que o jogador informou uma resposta não numérica ou inválida
Quando o servidor tenta interpretar a resposta
Então o sistema não deve quebrar a execução
E deve retornar `RESULTADO|ERRO`.

### Rastreabilidade

| Artefato                 | Referência                                                 |                    |       |
| ------------------------ | ---------------------------------------------------------- | ------------------ | ----- |
| PR de testes do servidor | PR #6 — Testes unitários adicionais para `ServidorService` |                    |       |
| PR de cenário adicional  | PR #7 — Teste simulando conexão com dois clientes          |                    |       |
| PR de consolidação       | PR #8 — Backend                                            |                    |       |
| Código impactado         | `Cliente.java` · `ServidorService.java` · `Pergunta.java`  |                    |       |
| Testes automatizados     | `ServidorServiceTest` · `PerguntaTest`                     |                    |       |
| Protocolo impactado      | `RESPONDA` · `RESULTADO                                    | ACERTO`·`RESULTADO | ERRO` |

### Notas Técnicas

O cliente continua lendo mensagens do servidor até receber `FIM`. Quando recebe `RESPONDA`, ele pausa a leitura, solicita a entrada do jogador e envia a resposta pelo socket. O servidor trata a resposta recebida e evita que entradas inválidas interrompam a execução.

### Definição de Pronto

* [x] Cliente identifica a mensagem `RESPONDA`.
* [x] Cliente envia a resposta digitada pelo jogador.
* [x] Servidor lê a resposta enviada.
* [x] Servidor valida acerto e erro.
* [x] Respostas inválidas são tratadas sem quebrar o fluxo.
* [x] Testes unitários cobrem cenários de acerto, erro e entrada inválida.

---

## US-4 — Cálculo de Pontuação e Ranking dos Jogadores

### Identificação

| Campo           | Valor                  |
| --------------- | ---------------------- |
| ID              | US-004                 |
| Épico           | Pontuação e Ranking    |
| Prioridade      | Alta                   |
| Status Final    | Parcialmente Concluído |
| Rastreabilidade | PR #5 · PR #8          |

### História de Usuário

Como jogador do MiniKahoot,
eu quero receber pontos ao acertar uma pergunta e ter minha pontuação organizada em um ranking,
para que meu desempenho na partida seja registrado e comparado com outros jogadores.

### Contexto / Motivação

A pontuação é uma parte central de qualquer jogo de quiz. No MiniKahoot, a classe `GerenciadorDePontos` foi criada para controlar jogadores, calcular pontuação e organizar o ranking. A regra considera o tempo de resposta como base da pontuação e impede pontuações negativas. O servidor já integra a pontuação ao fluxo da partida, retornando `PONTOS|...` ao cliente. O ranking existe no domínio, mas ainda não é exibido no protocolo final nem persistido em arquivo.

### Critérios de Aceitação

#### AC-01 — Pontuação inicial do jogador

Dado que um jogador foi cadastrado no gerenciador de pontos
Quando a partida ainda não teve acertos
Então a pontuação inicial do jogador deve ser `0`.

#### AC-02 — Pontuação ao acertar resposta

Dado que o jogador respondeu corretamente
Quando o servidor processa a resposta
Então o sistema deve adicionar pontos ao jogador
E o servidor deve retornar uma mensagem `PONTOS|<valor>`.

#### AC-03 — Ausência de pontuação ao errar

Dado que o jogador respondeu incorretamente
Quando o servidor processa a resposta
Então o sistema não deve adicionar pontos
E a pontuação retornada deve permanecer em `0`.

#### AC-04 — Bloqueio de pontuação negativa

Dado que o cálculo de pontos considera tempo de resposta
Quando o tempo informado ultrapassa o limite da regra
Então a pontuação calculada não deve ficar negativa
E o valor mínimo deve ser `0`.

#### AC-05 — Ordenação do ranking

Dado que existem jogadores com pontuações diferentes
Quando o sistema solicita o ranking no domínio
Então os jogadores devem ser retornados ordenados da maior para a menor pontuação.

#### AC-06 — Limitação atual do ranking

Dado que o ranking existe no domínio
Quando a partida termina no fluxo atual cliente-servidor
Então o ranking ainda não é enviado ao cliente
E deve ser documentado como melhoria futura a exibição do ranking completo ao final da partida.

### Rastreabilidade

| Artefato             | Referência                                                                                                                                                           |
| -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| PR de implementação  | PR #5 — Esqueleto do gerenciador de pontos                                                                                                                           |
| PR de consolidação   | PR #8 — Backend                                                                                                                                                      |
| Commits relacionados | `feat: implementa ranking de jogadores por pontuacao` · `test: adiciona testes de ranking no gerenciador de pontos` · `feat: integra pontuacao ao fluxo do servidor` |
| Código impactado     | `GerenciadorDePontos.java` · `ServidorService.java`                                                                                                                  |
| Testes automatizados | `GerenciadorDePontosTest` · `ServidorServiceTest`                                                                                                                    |
| Protocolo impactado  | `PONTOS`                                                                                                                                                             |

### Notas Técnicas

A regra de pontuação considera uma fórmula baseada em tempo e aplica proteção para impedir pontuação negativa. No fluxo atual do servidor, a pontuação integrada usa tempo fixo, gerando pontuação máxima quando o jogador acerta. A medição real do tempo de resposta e o envio do ranking completo ao cliente são melhorias futuras.

### Definição de Pronto

* [x] Gerenciador de pontos implementado.
* [x] Pontuação inicial definida como `0`.
* [x] Acertos adicionam pontos.
* [x] Erros não adicionam pontos.
* [x] Pontuação negativa é impedida.
* [x] Ranking ordenado existe no domínio.
* [x] Testes unitários cobrem pontuação e ranking.
* [ ] Ranking ainda precisa ser exibido ao cliente no final da partida.
* [ ] Tempo real de resposta ainda precisa ser medido no fluxo cliente-servidor.

---

## US-5 — Pipeline Jenkins em Docker com Testes, Relatórios e Artefatos

### Identificação

| Campo           | Valor                           |
| --------------- | ------------------------------- |
| ID              | US-005                          |
| Épico           | CI/CD e Evidências de Qualidade |
| Prioridade      | Alta                            |
| Status Final    | Concluído                       |
| Rastreabilidade | PR #2 · PR #8                   |

### História de Usuário

Como integrante do grupo de desenvolvimento,
eu quero que o projeto seja validado automaticamente por um pipeline Jenkins em container Docker,
para que build, testes, relatórios e artefatos sejam gerados de forma reproduzível antes da entrega.

### Contexto / Motivação

A NP2 exige CI/CD sem GitHub Actions, além de testes automatizados e relatórios entregues via pipeline. Para atender a essa exigência, o projeto adotou Jenkins em container Docker. O `Dockerfile.jenkins` prepara o ambiente com Jenkins, Java, Maven, Git e Docker CLI. O `docker-compose.yml` permite subir o Jenkins localmente. O `Jenkinsfile` organiza os estágios de validação do ambiente, build Maven, testes unitários, empacotamento, publicação de relatórios JUnit, arquivamento de artefatos e notificação por e-mail quando o SMTP estiver configurado.

### Critérios de Aceitação

#### AC-01 — Jenkins executando em container

Dado que o Docker está instalado no ambiente local
Quando o comando `docker compose up -d --build` é executado
Então o Jenkins deve subir em container
E a interface deve ficar acessível em `http://localhost:8080`.

#### AC-02 — Validação do ambiente

Dado que o pipeline Jenkins foi iniciado
Quando o stage de ambiente é executado
Então o Jenkins deve validar a disponibilidade de Java, Maven e Git.

#### AC-03 — Build Maven

Dado que o código está disponível na branch configurada
Quando o stage de build é executado
Então o Jenkins deve compilar o projeto com Maven
E a compilação deve terminar sem erro.

#### AC-04 — Execução dos testes unitários

Dado que existem testes em `src/test/java/br/com/kahoot`
Quando o stage de testes é executado
Então o Jenkins deve executar os testes unitários
E deve publicar os relatórios JUnit gerados em `target/surefire-reports`.

#### AC-05 — Geração de artefato

Dado que o build e os testes foram concluídos com sucesso
Quando o stage de package é executado
Então o Maven deve gerar o arquivo `.jar` em `target/`
E o Jenkins deve arquivar esse artefato como evidência.

#### AC-06 — Notificação por e-mail

Dado que o Jenkins possui SMTP configurado
Quando o pipeline finaliza com sucesso ou falha
Então o Jenkins deve enviar uma notificação por e-mail
E a ausência de SMTP deve ser documentada como limitação de ambiente, não como falha da regra de negócio.

### Rastreabilidade

| Artefato                      | Referência                                                                                                                                                                                                                                                      |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| PR de configuração inicial    | PR #2 — Backend README e `pom.xml`                                                                                                                                                                                                                              |
| PR de consolidação            | PR #8 — Backend                                                                                                                                                                                                                                                 |
| Commits relacionados          | `ci: adiciona Dockerfile do Jenkins` · `ci: adiciona docker compose para Jenkins` · `ci: adiciona stage de build Maven` · `ci: adiciona stage de testes unitarios` · `adicionado stage de package jar` · `ci: ajusta pipeline para testes e artefatos do maven` |
| Código/configuração impactada | `Jenkinsfile` · `Dockerfile.jenkins` · `docker-compose.yml` · `pom.xml`                                                                                                                                                                                         |
| Testes automatizados          | `PerguntaTest` · `BancoDePerguntasTest` · `GerenciadorDePontosTest` · `ServidorServiceTest`                                                                                                                                                                     |
| Evidências geradas            | Relatórios JUnit · Artefato `.jar` · Console Output do Jenkins                                                                                                                                                                                                  |

### Notas Técnicas

O projeto não utiliza GitHub Actions, respeitando a restrição da NP2. A execução do Jenkins depende do ambiente local com Docker. A notificação por e-mail depende de configuração SMTP manual no Jenkins, especialmente quando for usado Gmail com senha de app.

### Definição de Pronto

* [x] `Dockerfile.jenkins` criado.
* [x] `docker-compose.yml` criado.
* [x] `Jenkinsfile` criado.
* [x] Pipeline compila o projeto.
* [x] Pipeline executa testes unitários.
* [x] Pipeline publica relatórios JUnit.
* [x] Pipeline gera e arquiva artefato `.jar`.
* [x] README documenta execução local e Jenkins.
* [x] SMTP deve ser configurado no ambiente Jenkins caso a equipe demonstre notificação por e-mail.
