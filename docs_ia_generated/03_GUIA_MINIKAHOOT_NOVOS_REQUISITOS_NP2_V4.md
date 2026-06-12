# MiniKahoot — Guia 03 Atualizado V3: Novos Requisitos da NP2 com Jenkins em Container Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**CI/CD adotado:** Jenkins em container Docker  
**Base da revisão:** código atual enviado em `minikahoot_v4.zip`  
**Objetivo:** atualizar os requisitos da NP2 conforme a versão mais recente do projeto, refletindo o fluxo real cliente-servidor, os testes existentes, o Jenkins com notificação por e-mail, a organização do repositório no GitHub e as evidências de commits por integrante.

---

## 1. Estado atual do projeto para a NP2

A versão atual do MiniKahoot está mais completa do que a versão usada no Guia 03 V2. Agora o projeto já possui fluxo básico de quiz integrado entre servidor e cliente.

## O que existe no código atual

- Aplicação Java 17 com Maven.
- Servidor TCP na porta `12345`.
- Cliente de terminal que conecta em `localhost:12345`.
- Protocolo simples de mensagens em texto.
- Envio de boas-vindas pelo servidor.
- Envio de pergunta e alternativas pelo servidor.
- Leitura de resposta enviada pelo cliente.
- Validação de resposta correta/incorreta.
- Envio de resultado (`RESULTADO|ACERTO` ou `RESULTADO|ERRO`).
- Envio de pontuação (`PONTOS|...`).
- Encerramento do atendimento com `FIM`.
- Classe `Pergunta` com validações de domínio.
- Classe `BancoDePerguntas` com 3 perguntas iniciais.
- Classe `GerenciadorDePontos` com pontuação baseada em tempo, proteção contra pontuação negativa e método de ranking.
- Testes unitários com JUnit 5 e Mockito.
- Jenkins em container Docker.
- Pipeline com validação de ambiente, build, testes, package, relatórios JUnit, artefatos e notificação por e-mail.
- README atualizado com protocolo, execução e observação sobre SMTP.
- `.gitignore` configurado para ignorar `target/`, arquivos compactados e arquivos de IDE.

## O que não existe ou ainda não está completo

- `ClienteGUI.java`.
- Interface JavaFX.
- Classe `Ranking.java` separada.
- Arquivo `ranking.txt`.
- Persistência de ranking.
- Exibição do ranking final no protocolo cliente-servidor.
- Atendimento de múltiplos clientes simultâneos.
- Medição real do tempo de resposta no fluxo do servidor.

Esses itens não devem ser apresentados como completamente implementados. Eles podem ser citados como melhorias futuras.

---

# 2. Estrutura atual do projeto

A estrutura principal esperada para o repositório no GitHub é:

```text
.
|-- Dockerfile.jenkins
|-- Jenkinsfile
|-- README.md
|-- docker-compose.yml
|-- pom.xml
|-- .gitignore
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

## Arquivos que não devem ser versionados

Como o projeto está no GitHub, a entrega principal deve ser o próprio repositório. Portanto, estes arquivos/pastas devem ficar fora do versionamento:

```text
target/
*.class
*.jar
*.zip
.idea/
.vscode/
*.log
.github/modernize/
```

O `.gitignore` já contempla esses itens. Mesmo assim, se algum deles já tiver sido enviado para o GitHub antes do `.gitignore`, é necessário remover do versionamento com `git rm --cached`.

Exemplo:

```bash
git rm -r --cached target
git rm --cached *.zip
git add .gitignore
git commit -m "chore: remove arquivos gerados do versionamento"
git push
```

---

# 3. Protocolo atual de comunicação

O servidor e o cliente usam mensagens de texto via socket TCP.

Exemplo de fluxo com resposta correta:

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

## Interpretação do fluxo

| Mensagem | Função |
|---|---|
| `BEM_VINDO` | Inicia o atendimento do cliente. |
| `PERGUNTA` | Envia o enunciado da pergunta. |
| `ALT` | Envia uma alternativa numerada. |
| `FIM_PERGUNTA` | Indica que todas as alternativas foram enviadas. |
| `RESPONDA` | Solicita que o cliente envie uma resposta. |
| `RESULTADO` | Informa se o jogador acertou ou errou. |
| `PONTOS` | Informa a pontuação atual do jogador. |
| `FIM` | Finaliza o atendimento. |

---

# 4. Histórias de usuário atualizadas

Cada história deve ter critério de aceitação, prioridade, status e rastreabilidade.

---

## História 1 — Conectar cliente ao servidor

**Como** jogador,  
**eu quero** iniciar o cliente e conectar ao servidor,  
**para que** eu possa participar da partida.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `Servidor.java` abre a porta `12345`.
- `Cliente.java` conecta em `localhost:12345`.
- `ServidorService.java` atende o socket recebido.
- `ServidorServiceTest` valida o envio da mensagem inicial.

### Critérios de aceitação

```gherkin
Given que o servidor está em execução na porta 12345
When o jogador executa o cliente
Then o cliente deve se conectar ao servidor
And deve receber a mensagem de boas-vindas
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#01 - Conexão cliente-servidor` |
| PR | `PR #01 - Implementa conexão TCP básica` |
| Código | `Cliente.java`, `Servidor.java`, `ServidorService.java` |
| Teste | `ServidorServiceTest.deveEnviarMensagemDeBoasVindasAoSocket` |
| CI/CD | Relatório JUnit publicado no Jenkins |

---

## História 2 — Receber pergunta e alternativas

**Como** jogador,  
**eu quero** receber uma pergunta com alternativas,  
**para que** eu consiga escolher uma resposta durante a partida.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `BancoDePerguntas.java` carrega perguntas iniciais.
- `ServidorService.java` obtém a primeira pergunta do banco.
- `ServidorService.java` envia `PERGUNTA`, `ALT` e `FIM_PERGUNTA`.
- `Cliente.java` lê todas as mensagens enviadas pelo servidor até `FIM`.
- `ServidorServiceTest.deveEnviarPerguntaEAlternativasParaCliente` valida o protocolo esperado.

### Critérios de aceitação

```gherkin
Given que o jogador está conectado
When o servidor inicia o quiz
Then o cliente deve receber o enunciado da pergunta
And deve receber todas as alternativas disponíveis
And deve identificar quando a pergunta terminou
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#02 - Envio de pergunta e alternativas` |
| PR | `PR #02 - Integra banco de perguntas ao servidor` |
| Código | `BancoDePerguntas.java`, `ServidorService.java`, `Cliente.java` |
| Teste | `ServidorServiceTest.deveEnviarPerguntaEAlternativasParaCliente` |
| CI/CD | Jenkins executando `mvn clean test` |

---

## História 3 — Enviar e validar resposta

**Como** jogador,  
**eu quero** enviar uma resposta ao servidor,  
**para que** o sistema diga se eu acertei ou errei.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `Cliente.java` envia a resposta quando recebe `RESPONDA`.
- `ServidorService.java` lê a resposta com `readLine()`.
- `ServidorService.java` trata resposta numérica, resposta incorreta e resposta não numérica.
- `Pergunta.java` possui `verificarResposta(int resposta)`.
- `ServidorServiceTest` cobre acerto, erro e resposta não numérica.

### Critérios de aceitação

```gherkin
Given que o cliente recebeu uma pergunta
When o servidor solicita a resposta
Then o cliente deve enviar a resposta digitada pelo jogador
And o servidor deve validar se a resposta está correta
And o servidor deve retornar ACERTO ou ERRO
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#03 - Validação de respostas` |
| PR | `PR #03 - Implementa leitura e validação de resposta` |
| Código | `Cliente.java`, `ServidorService.java`, `Pergunta.java` |
| Teste | `ServidorServiceTest.deveEnviarResultadoDeErroQuandoRespostaIncorreta`, `ServidorServiceTest.deveEnviarResultadoDeErroQuandoRespostaNaoForNumerica` |
| CI/CD | Build Jenkins aprovado |

---

## História 4 — Calcular pontuação do jogador

**Como** jogador,  
**eu quero** receber pontos quando acerto uma pergunta,  
**para que** meu desempenho seja contabilizado.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `GerenciadorDePontos.java` calcula a pontuação com base em tempo.
- `GerenciadorDePontos.java` impede pontuação negativa usando `Math.max(0, ...)`.
- `ServidorService.java` adiciona pontos quando a resposta está correta.
- `ServidorService.java` envia `PONTOS|...` ao cliente.
- `GerenciadorDePontosTest` valida pontuação inicial, acúmulo, resposta rápida e bloqueio de pontuação negativa.

### Observação importante

No fluxo atual do servidor, a chamada para pontuar usa tempo fixo `0`, gerando pontuação máxima de `1500` quando o jogador acerta. A regra de pontuação por tempo existe no domínio, mas a medição real de tempo de resposta ainda é melhoria futura.

### Critérios de aceitação

```gherkin
Given que o jogador respondeu corretamente
When o servidor processa a resposta
Then o sistema deve adicionar pontos ao jogador
And deve retornar a pontuação atual no protocolo
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#04 - Sistema de pontuação` |
| PR | `PR #04 - Integra pontuação ao fluxo do servidor` |
| Código | `GerenciadorDePontos.java`, `ServidorService.java` |
| Teste | `GerenciadorDePontosTest`, `ServidorServiceTest.deveEnviarPerguntaEAlternativasParaCliente` |
| CI/CD | Relatório JUnit publicado no Jenkins |

---

## História 5 — Visualizar ranking da partida

**Como** jogador,  
**eu quero** visualizar um ranking da partida,  
**para que** eu saiba minha colocação em relação aos outros jogadores.

### Prioridade

Média.

### Status

Parcialmente implementada.

### Evidência no código

- `GerenciadorDePontos.java` possui o método `obterRanking()`.
- `GerenciadorDePontosTest.deveRetornarRankingOrdenadoPorMaiorPontuacao` valida a ordenação.
- `GerenciadorDePontosTest.naoDevePermitirAlterarRankingExternamente` valida proteção contra alteração externa.

### Limitação atual

O ranking existe no domínio, mas ainda não é enviado pelo `ServidorService`, não é exibido pelo `Cliente` e não é persistido em arquivo. Portanto, deve ser apresentado como funcionalidade parcial.

### Critérios de aceitação atuais

```gherkin
Given que existem jogadores com pontuação
When o sistema solicita o ranking no domínio
Then o gerenciador de pontos deve retornar os jogadores ordenados pela maior pontuação
```

### Critérios de aceitação futuros

```gherkin
Given que a partida terminou
When o servidor finaliza o atendimento
Then o servidor deve enviar o ranking ao cliente
And o cliente deve exibir o ranking final
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#05 - Ranking de jogadores` |
| PR | `PR #05 - Implementa ranking no gerenciador de pontos` |
| Código | `GerenciadorDePontos.java` |
| Teste | `GerenciadorDePontosTest.deveRetornarRankingOrdenadoPorMaiorPontuacao` |
| CI/CD | Jenkins arquivando `.jar` após testes |

---

## História 6 — Executar testes automaticamente no Jenkins

**Como** integrante do grupo,  
**eu quero** que os testes sejam executados automaticamente no Jenkins,  
**para que** o grupo identifique falhas antes do merge para a `main`.

### Prioridade

Alta.

### Status

Implementada na configuração.

### Evidência no código

- `Dockerfile.jenkins` usa `jenkins/jenkins:lts-jdk17` e instala Maven, Git e Docker.
- `docker-compose.yml` sobe Jenkins nas portas `8080` e `50000`.
- `Jenkinsfile` executa validação de ambiente, build, testes, package, relatórios, artefatos e validação final.
- O bloco `post` do `Jenkinsfile` envia e-mail em caso de sucesso ou falha, desde que o SMTP esteja configurado.

### Critérios de aceitação

```gherkin
Given que existe uma alteração na branch Backend
When o Jenkins executa o pipeline
Then o projeto deve ser compilado
And os testes unitários devem ser executados
And os relatórios JUnit devem ser publicados
And o artefato .jar deve ser arquivado
And a notificação por e-mail deve ser enviada se o SMTP estiver configurado
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#06 - Pipeline Jenkins em Docker` |
| PR | `PR #06 - Adiciona Jenkinsfile e Docker` |
| Código | `Jenkinsfile`, `Dockerfile.jenkins`, `docker-compose.yml` |
| Teste | Relatórios JUnit publicados pelo Jenkins |
| CI/CD | Pipeline Jenkins em container Docker |

---

# 5. Testes e evidências atuais

A estrutura atual possui 4 classes de teste:

```text
BancoDePerguntasTest.java
GerenciadorDePontosTest.java
PerguntaTest.java
ServidorServiceTest.java
```

Pelos relatórios presentes em `target/surefire-reports`, há evidência de:

| Classe de teste | Quantidade | Falhas |
|---|---:|---:|
| `BancoDePerguntasTest` | 7 | 0 |
| `GerenciadorDePontosTest` | 14 | 0 |
| `PerguntaTest` | 8 | 0 |
| `ServidorServiceTest` | 7 | 0 |
| **Total** | **36** | **0** |

## Validação recomendada antes da entrega

Mesmo existindo relatórios antigos, o correto antes da entrega é rodar novamente:

```bash
mvn clean test
mvn clean package
```

Isso garante que os relatórios atuais sejam gerados do zero e que o artefato `.jar` esteja atualizado.

---

# 6. Metodologia de desenvolvimento

## Metodologia adotada

Sugestão: **Kanban com ciclos semanais**.

Essa metodologia combina com o projeto porque a equipe pode organizar pequenas entregas incrementais: domínio, pontuação, comunicação cliente-servidor, testes, Jenkins, documentação e limpeza do repositório.

## Justificativa

O Kanban permite:

- Visualizar tarefas pendentes, em andamento e concluídas.
- Dividir trabalho entre os integrantes.
- Priorizar correções antes de novas funcionalidades.
- Relacionar issues, PRs e testes.
- Validar cada mudança no Jenkins.
- Preparar evidências para a defesa.
- Manter rastreabilidade no GitHub.

## Ferramenta de acompanhamento

Como o trabalho está no GitHub, a opção mais coerente é:

```text
GitHub Issues + Pull Requests + Jenkins em Docker
```

## Quadro Kanban sugerido

```text
Backlog
↓
Preparado
↓
Em desenvolvimento
↓
Em revisão
↓
Testado no Jenkins
↓
Concluído
```

---

## Papéis do grupo

| Papel | Responsável | Responsabilidade |
|---|---|---|
| Organização / requisitos | Integrante 1 | Histórias de usuário, issues, PRs e documentação |
| Dev domínio | Integrante 2 | `Pergunta`, `BancoDePerguntas` e validações |
| Dev pontuação / testes | Integrante 3 | `GerenciadorDePontos`, ranking e testes unitários |
| Dev infraestrutura / CI-CD | Integrante 4 | `Servidor`, `Cliente`, Jenkins, Docker, SMTP e README |

> Mesmo com papéis definidos, todos devem entender o projeto inteiro para a defesa Q&A.

---

## Definition of Ready — DoR

Uma tarefa só pode começar quando:

- Possui descrição clara.
- Possui responsável definido.
- Possui critério de aceitação.
- Está ligada a uma história de usuário ou requisito.
- Possui ideia de teste associado.
- Tem impacto claro no código, teste, documentação ou pipeline.
- Não usa GitHub Actions, conforme restrição da NP2.

## Definition of Done — DoD

Uma tarefa só é considerada pronta quando:

- Código implementado.
- Testes unitários criados ou atualizados.
- `mvn clean test` passando localmente.
- Jenkins em Docker executando com sucesso.
- Relatórios JUnit publicados.
- Artefato `.jar` gerado quando aplicável.
- PR revisado por pelo menos um integrante.
- README atualizado, se necessário.
- Issue vinculada fechada.
- SMTP configurado, caso a equipe mantenha notificação por e-mail no pipeline.

---

# 7. Dinâmica de desenvolvimento

## Fluxo de branches

O grupo usa:

```text
main
Backend
```

Uso recomendado:

- `Backend`: desenvolvimento contínuo.
- `main`: versão estável.
- PR de `Backend` para `main` antes da entrega.
- Jenkins validando a branch `Backend`.

## Padrão de commits

```text
feat: nova funcionalidade
fix: correção
test: teste unitário
docs: documentação
refactor: refatoração
ci: pipeline e automação
chore: ajustes de organização
```

Exemplos adequados ao código atual:

```bash
git commit -m "feat: integra envio de pergunta ao servidor"
git commit -m "feat: permite cliente responder pergunta"
git commit -m "feat: integra pontuacao ao fluxo do servidor"
git commit -m "test: adiciona testes do protocolo cliente-servidor"
git commit -m "test: adiciona testes de ranking no gerenciador de pontos"
git commit -m "docs: atualiza README com protocolo do MiniKahoot"
git commit -m "ci: configura notificacao por email no Jenkinsfile"
git commit -m "chore: atualiza gitignore do projeto"
```

## Code review

Cada PR deve ter:

- Descrição do que foi alterado.
- Issue relacionada.
- Evidência de teste local.
- Jenkins passando.
- Relatórios JUnit disponíveis.
- Artefato `.jar`, quando aplicável.
- Comentário ou aprovação de pelo menos um colega.

---

# 8. Jenkins em Docker e SMTP

## Arquivos de CI/CD

```text
Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
```

## Como subir o Jenkins

```bash
docker compose up -d --build
```

Depois acessar:

```text
http://localhost:8080
```

## Stages atuais do pipeline

| Stage | Função |
|---|---|
| `Ambiente - Java Maven Git` | Verifica Java, Maven e Git. |
| `Build Maven` | Executa `mvn clean compile`. |
| `Testes Unitarios` | Executa `mvn clean test` e publica JUnit. |
| `Package JAR` | Executa `mvn package`. |
| `Relatorios e Artefatos` | Arquiva `.jar` e garante publicação dos relatórios. |
| `Validacao Final` | Mostra resumo da execução. |

## Evidência de job/stage por integrante

A especificação da NP2 pede pipeline com pelo menos 1 job por integrante, comitado pelo próprio integrante. No Jenkins, a forma mais prática para o projeto é deixar o `Jenkinsfile` com stages separados por responsabilidade e garantir que cada integrante tenha pelo menos um commit relevante relacionado à sua parte.

> Se o professor exigir literalmente **jobs separados** no Jenkins, criar quatro jobs no Jenkins apontando para o mesmo repositório, cada um executando uma etapa específica. Se ele aceitar evidência no `Jenkinsfile`, usar stages nomeados por área, como no exemplo abaixo.

Sugestão de divisão no pipeline:

| Integrante | Job/Stage sugerido | Comando Maven sugerido | Evidência |
|---|---|---|---|
| Integrante 1 | `Validacao Dominio Perguntas` | `mvn -Dtest=PerguntaTest,BancoDePerguntasTest test` | Testes do domínio de perguntas. |
| Integrante 2 | `Validacao Pontuacao Ranking` | `mvn -Dtest=GerenciadorDePontosTest test` | Testes de pontuação e ranking. |
| Integrante 3 | `Validacao Protocolo Servidor` | `mvn -Dtest=ServidorServiceTest test` | Testes do protocolo cliente-servidor. |
| Integrante 4 | `Build Package Evidencias` | `mvn clean package` | Build, artefato `.jar`, relatórios JUnit e Jenkins/Docker. |

Exemplo de stages adicionais no `Jenkinsfile`:

```groovy
stage('Validacao Dominio Perguntas') {
    steps {
        sh 'mvn -Dtest=PerguntaTest,BancoDePerguntasTest test'
    }
}

stage('Validacao Pontuacao Ranking') {
    steps {
        sh 'mvn -Dtest=GerenciadorDePontosTest test'
    }
}

stage('Validacao Protocolo Servidor') {
    steps {
        sh 'mvn -Dtest=ServidorServiceTest test'
    }
}

stage('Build Package Evidencias') {
    steps {
        sh 'mvn clean package'
    }
}
```

Essa divisão ajuda na defesa porque permite apontar diretamente onde cada área do projeto é validada no pipeline.

## Configuração de SMTP para o bloco `mail`

O `Jenkinsfile` atual mantém notificação por e-mail. Para isso funcionar, o Jenkins precisa de SMTP configurado.

Caminho no Jenkins:

```text
Manage Jenkins
→ System
→ E-mail Notification
```

Configuração comum usando Gmail:

```text
SMTP server: smtp.gmail.com
Use SMTP Authentication: marcado
User Name: seu_email@gmail.com
Password: senha de app do Gmail
Use SSL: marcado
SMTP Port: 465
Charset: UTF-8
```

Também configurar:

```text
Manage Jenkins
→ System
→ Jenkins Location
→ System Admin e-mail address
```

Exemplo:

```text
System Admin e-mail address: seu_email@gmail.com
```

## Observação importante

No Gmail, não se usa a senha normal da conta. É necessário ativar verificação em duas etapas e criar uma senha de app.

## Teste de configuração

Na própria seção `E-mail Notification`, usar:

```text
Test configuration by sending test e-mail
```

Se o teste falhar, o pipeline pode compilar e testar corretamente, mas falhar no bloco `post` ao tentar enviar e-mail. Portanto, a configuração SMTP deve ser testada antes da defesa.

---

# 9. Uso de Inteligência Artificial

A NP2 exige uma seção obrigatória no README chamada:

```md
## Uso de IA
```

## Modelo atualizado para o README

```md
## Uso de IA

Durante o desenvolvimento do MiniKahoot, o grupo utilizou ferramentas de Inteligência Artificial como apoio para planejamento, revisão de código, organização dos testes, documentação, depuração e configuração de CI/CD.

### Modelos utilizados

- ChatGPT
- GitHub Copilot, se aplicável
- Gemini ou Claude, se aplicável

### Finalidades de uso

- Sugestão de melhorias para o código Java.
- Revisão da estrutura Maven.
- Apoio na criação de testes unitários com JUnit 5 e Mockito.
- Apoio na configuração do Jenkins em container Docker.
- Apoio na configuração de SMTP no Jenkins.
- Apoio na escrita do README.
- Organização de histórias de usuário e critérios de aceitação.
- Revisão da coerência entre código, testes, pipeline e documentação.

### Exemplos de prompts utilizados

1. "Revise o código atual do MiniKahoot e atualize os guias conforme a estrutura atual."
2. "Como configurar Jenkins em Docker para um projeto Maven Java 17?"
3. "Quais testes unitários fazem sentido para Pergunta, BancoDePerguntas e GerenciadorDePontos?"
4. "Como documentar o uso de IA na entrega da NP2?"
5. "Como organizar histórias de usuário para uma aplicação cliente/servidor de quiz?"
6. "Como configurar SMTP no Jenkins para manter notificação por e-mail?"

### Respostas aceitas, ajustadas ou descartadas

- Aceitamos a sugestão de organizar o trabalho em domínio, pontuação, comunicação e CI/CD.
- Ajustamos os guias para refletir o código real, sem apresentar JavaFX como implementado.
- Aceitamos a sugestão de Jenkins em Docker porque a NP2 não permite GitHub Actions.
- Aceitamos a manutenção do e-mail no Jenkins, desde que SMTP seja configurado corretamente.
- Ajustamos os testes sugeridos para focar nas classes existentes.
- Descartamos sugestões de deploy em produção, pois o projeto é acadêmico e local.

### O que não foi feito por IA

- Execução final do projeto no computador do grupo.
- Configuração final do Jenkins no ambiente local.
- Validação final do Docker Desktop.
- Criação real das senhas de app ou configuração SMTP na conta do grupo.
- Commits no repositório.
- Revisão final dos PRs.
- Defesa oral do projeto.
```

---

# 10. Preparação para defesa Q&A

## Perguntas prováveis do professor

| Pergunta | Resposta esperada |
|---|---|
| Qual é o objetivo do projeto? | Demonstrar uma aplicação cliente/servidor em Java usando sockets TCP para um quiz simples. |
| O cliente é gráfico? | Não. A versão atual usa cliente via terminal. |
| O servidor envia perguntas? | Sim. O servidor envia uma pergunta e suas alternativas usando o protocolo definido. |
| O cliente responde ao servidor? | Sim. Quando recebe `RESPONDA`, o cliente envia a resposta digitada pelo jogador. |
| Como a resposta correta é validada? | Pelo método `verificarResposta(int resposta)` da classe `Pergunta`, chamado pelo `ServidorService`. |
| Como a pontuação é calculada? | Pelo `GerenciadorDePontos`, usando `100 * max(0, 15 - tempoPercorrido)`. |
| A pontuação pode ficar negativa? | Não. A versão atual usa `Math.max(0, ...)` para impedir pontuação negativa. |
| O tempo real da resposta é medido? | Ainda não. O servidor usa tempo fixo `0` na integração atual; medir o tempo real é melhoria futura. |
| Existe ranking? | Parcialmente. Existe método de ranking em `GerenciadorDePontos`, mas ele ainda não é exibido pelo cliente nem persistido. |
| Existe ranking persistido? | Não. Não há `ranking.txt` nem persistência em arquivo. |
| Por que Jenkins? | Porque a NP2 exige CI/CD sem GitHub Actions. |
| Por que Docker? | Para tornar o ambiente Jenkins reproduzível e fácil de subir. |
| Onde está o pipeline? | No `Jenkinsfile`. |
| Onde estão os testes? | Em `src/test/java/br/com/kahoot`. |
| Como rodo os testes? | `mvn clean test` na pasta onde está o `pom.xml`. |
| Como o Jenkins publica relatórios? | Com `junit 'target/surefire-reports/*.xml'`. |
| Como o Jenkins publica artefato? | Com `archiveArtifacts artifacts: 'target/*.jar'`. |
| O e-mail do Jenkins sempre funciona? | Só se SMTP estiver configurado no Jenkins. |
| O que deve ficar no GitHub? | Código-fonte, testes, `pom.xml`, README, Jenkinsfile, Dockerfile, docker-compose e `.gitignore`; não `target/` nem ZIPs gerados. |
| Como a IA foi usada? | Como apoio para planejamento, revisão, testes, documentação e CI/CD, com revisão final do grupo. |

---

# 11. Checklist atualizado dos requisitos da NP2

- [ ] Criar pelo menos 5 histórias de usuário.
- [ ] Escrever critérios de aceitação em Given/When/Then.
- [ ] Indicar prioridade de cada história.
- [ ] Indicar status real de cada história.
- [ ] Não apresentar JavaFX como implementado.
- [ ] Não apresentar ranking persistido como implementado.
- [ ] Apresentar ranking como parcial: existe no domínio, mas não no protocolo final.
- [ ] Criar rastreabilidade história → issue/PR → teste.
- [ ] Explicar metodologia adotada.
- [ ] Definir papéis do grupo.
- [ ] Definir DoR e DoD.
- [ ] Documentar fluxo de branches.
- [ ] Documentar code review.
- [ ] Documentar bloqueios e soluções.
- [ ] Criar seção `Uso de IA` no README.
- [ ] Listar modelos usados.
- [ ] Listar prompts reais.
- [ ] Explicar respostas aceitas, ajustadas e descartadas.
- [ ] Criar ou manter `Dockerfile.jenkins`.
- [ ] Criar ou manter `docker-compose.yml`.
- [ ] Criar ou manter `Jenkinsfile`.
- [ ] Garantir pelo menos 1 job/stage de pipeline relacionado a cada integrante.
- [ ] Garantir commits significativos de todos os integrantes, não apenas alterações em README.
- [ ] Demonstrar Jenkins rodando em container.
- [ ] Demonstrar Jenkins rodando pipeline.
- [ ] Demonstrar relatórios JUnit no Jenkins.
- [ ] Demonstrar artefato `.jar` publicado.
- [ ] Configurar SMTP se a notificação por e-mail for demonstrada.
- [ ] Atualizar README com protocolo real.
- [ ] Garantir que `target/` e ZIPs não estejam versionados no GitHub.
- [ ] Rodar `mvn clean test` antes da entrega.
- [ ] Rodar `mvn clean package` antes da entrega.
- [ ] Preparar todos para defesa Q&A.

---

# 12. Commits por integrante e evidências no GitHub

A NP2 avalia contribuição significativa de todos os membros. Portanto, cada integrante deve ter commits com impacto real no projeto, preferencialmente ligados a código, testes, CI/CD, refactoring ou configuração. Alterações apenas em README podem ajudar na documentação, mas não devem ser a única contribuição de um integrante.

Use os nomes reais dos integrantes no GitHub Issues, nos PRs e, se possível, nas descrições das tarefas. Os exemplos abaixo usam `Integrante 1`, `Integrante 2`, `Integrante 3` e `Integrante 4` como placeholders.

---

## Integrante 1 — Domínio de perguntas e validações

### Responsabilidade

Manter a base do quiz: `Pergunta`, `BancoDePerguntas`, validações e testes do domínio.

### Arquivos principais

```text
src/main/java/br/com/kahoot/Pergunta.java
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/test/java/br/com/kahoot/PerguntaTest.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
```

### Commits sugeridos

```bash
git add src/main/java/br/com/kahoot/Pergunta.java src/test/java/br/com/kahoot/PerguntaTest.java
git commit -m "test: reforca validacoes da classe pergunta"

git add src/main/java/br/com/kahoot/BancoDePerguntas.java src/test/java/br/com/kahoot/BancoDePerguntasTest.java
git commit -m "test: valida banco de perguntas inicial"

git add src/main/java/br/com/kahoot/Pergunta.java src/main/java/br/com/kahoot/BancoDePerguntas.java
git commit -m "refactor: organiza dominio de perguntas do quiz"
```

### Evidência esperada

- Issue relacionada à história 2 ou 3.
- PR com testes de `PerguntaTest` e `BancoDePerguntasTest`.
- Jenkins executando os testes do domínio.
- Comentário no PR explicando as validações adicionadas.

---

## Integrante 2 — Pontuação, proteção contra pontuação negativa e ranking

### Responsabilidade

Manter as regras de pontuação, ranking em memória e testes associados.

### Arquivos principais

```text
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
```

### Commits sugeridos

```bash
git add src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "fix: impede pontuacao negativa no gerenciador"

git add src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "feat: adiciona ranking ordenado por pontuacao"

git add src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
git commit -m "test: cobre pontuacao e ranking de jogadores"
```

### Evidência esperada

- Issue relacionada à história 4 ou 5.
- PR mostrando regra de pontuação e ranking.
- Testes demonstrando acúmulo de pontos, resposta rápida, pontuação mínima zero e ranking ordenado.
- Jenkins validando `GerenciadorDePontosTest`.

---

## Integrante 3 — Cliente, servidor e protocolo do jogo

### Responsabilidade

Integrar o fluxo real do jogo via socket: servidor envia pergunta, cliente responde, servidor valida e retorna resultado.

### Arquivos principais

```text
src/main/java/br/com/kahoot/Cliente.java
src/main/java/br/com/kahoot/Servidor.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

### Commits sugeridos

```bash
git add src/main/java/br/com/kahoot/ServidorService.java
git commit -m "feat: servidor envia pergunta e alternativas"

git add src/main/java/br/com/kahoot/Cliente.java
git commit -m "feat: cliente envia resposta ao servidor"

git add src/main/java/br/com/kahoot/ServidorService.java
git commit -m "feat: servidor valida resposta e retorna pontuacao"

git add src/test/java/br/com/kahoot/ServidorServiceTest.java
git commit -m "test: cobre protocolo cliente-servidor"
```

### Evidência esperada

- Issue relacionada às histórias 1, 2 e 3.
- PR com explicação do protocolo `BEM_VINDO`, `PERGUNTA`, `ALT`, `RESPONDA`, `RESULTADO`, `PONTOS` e `FIM`.
- Testes cobrindo acerto, erro e resposta não numérica.
- Demonstração ao vivo do servidor e cliente durante a defesa, se solicitado.

---

## Integrante 4 — CI/CD, Jenkins, Docker, SMTP e organização do GitHub

### Responsabilidade

Garantir build reproduzível, pipeline sem GitHub Actions, Jenkins em Docker, relatórios JUnit, artefato `.jar`, notificação por e-mail e repositório limpo.

### Arquivos principais

```text
Jenkinsfile
Dockerfile.jenkins
docker-compose.yml
.gitignore
README.md
```

### Commits sugeridos

```bash
git add Dockerfile.jenkins docker-compose.yml
git commit -m "ci: configura jenkins em container docker"

git add Jenkinsfile
git commit -m "ci: adiciona pipeline maven com testes e artefatos"

git add Jenkinsfile
git commit -m "ci: configura notificacao por email no jenkins"

git add .gitignore
git commit -m "chore: atualiza gitignore do projeto"

git add README.md
git commit -m "docs: documenta execucao jenkins docker e smtp"
```

### Evidência esperada

- Issue relacionada à história 6.
- PR com `Dockerfile.jenkins`, `docker-compose.yml` e `Jenkinsfile`.
- Jenkins executando build, testes, relatórios e artefatos.
- Evidência de configuração SMTP ou explicação clara de que o envio depende da configuração local do Jenkins.
- Repositório sem `target/`, arquivos `.class`, `.jar` gerado ou `.zip` versionado.

---

## Commits de limpeza, se arquivos gerados já foram enviados

Executar apenas se `target/`, `.zip` ou arquivos gerados aparecerem no `git status` ou no GitHub.

```bash
git rm -r --cached target
git commit -m "chore: remove arquivos gerados pelo maven"

git rm --cached *.zip
git commit -m "chore: remove arquivos compactados do repositorio"
```

Se o comando `git rm --cached *.zip` não encontrar arquivos, não há problema. Isso significa que não existem ZIPs versionados no diretório atual.

---

# 13. Validação final antes do PR ou merge para `main`

Antes de abrir o PR final ou fazer merge da branch `Backend` para `main`, rodar:

```bash
mvn clean test
mvn clean package
git status
```

O resultado esperado é:

```text
BUILD SUCCESS
```

E o `git status` deve mostrar:

```text
nothing to commit, working tree clean
```

## Checklist final de evidências para a defesa

- [ ] Pelo menos 5 histórias de usuário no guia ou README.
- [ ] Critérios de aceitação em Given/When/Then.
- [ ] Rastreabilidade história → issue/PR → teste.
- [ ] Commits significativos dos 4 integrantes.
- [ ] PRs com descrição e revisão.
- [ ] Jenkins em Docker funcionando.
- [ ] Pipeline sem GitHub Actions.
- [ ] Pelo menos 1 job/stage de pipeline relacionado a cada integrante.
- [ ] Relatórios JUnit publicados.
- [ ] Artefato `.jar` arquivado.
- [ ] README com instalação, execução, uso, funcionalidades e Uso de IA.
- [ ] Explicação honesta sobre ranking parcial e ausência de persistência.
- [ ] Explicação honesta sobre tempo fixo `0` na pontuação do servidor.
- [ ] SMTP configurado se o grupo for demonstrar e-mail no Jenkins.
- [ ] Todos os integrantes preparados para Q&A.
