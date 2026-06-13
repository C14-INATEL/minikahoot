# MiniKahoot — Guia 03 Atualizado: Novos Requisitos da NP2 com Jenkins em Container Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**CI/CD adotado:** Jenkins em container Docker  
**Base da revisão:** código atual enviado em `minikahoot(2).zip`  
**Objetivo:** atualizar os requisitos da NP2 conforme o código atual, sem assumir funcionalidades que não existem mais nesta versão, como cliente JavaFX, ranking persistido ou classe `Ranking`.

---

## 1. Estado atual do projeto para a NP2

O projeto atual possui uma base boa para a entrega da NP2, principalmente em relação a Maven, testes unitários e Jenkins em Docker.

## O que existe no código atual

- Aplicação Java 17 com Maven.
- Servidor TCP na porta `12345`.
- Cliente de terminal que conecta em `localhost:12345`.
- Classe `Pergunta` com validações.
- Classe `BancoDePerguntas` com perguntas iniciais.
- Classe `GerenciadorDePontos` com pontuação baseada em tempo.
- Classe `ServidorService` para envio de mensagem de boas-vindas.
- Testes com JUnit 5 e Mockito.
- Jenkins em container Docker.
- Pipeline com build, testes, relatórios JUnit e artefatos.

## O que não existe nesta versão atual

- `ClienteGUI.java`.
- Interface JavaFX.
- `Ranking.java`.
- `ranking.txt`.
- Fluxo completo de envio de perguntas pelo servidor.
- Leitura de respostas pelo cliente.
- Persistência de ranking.

Esses itens não devem ser apresentados como implementados. Podem aparecer como melhorias futuras ou tarefas planejadas.

---

# 2. Histórias de usuário atualizadas

Cada história deve ter critério de aceitação, prioridade, status e rastreabilidade. Os status abaixo foram ajustados conforme o código atual.

---

## História 1 — Conectar cliente ao servidor

**Como** jogador,  
**eu quero** iniciar o cliente e conectar ao servidor,  
**para que** eu receba a mensagem inicial do MiniKahoot.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `Servidor.java` abre a porta `12345`.
- `Cliente.java` conecta em `localhost:12345`.
- `ServidorService.java` envia a mensagem `Bem-vindo ao MiniKahoot!`.
- `ServidorServiceTest` valida o envio da mensagem.

### Critérios de aceitação

```gherkin
Given que o servidor está em execução na porta 12345
When o jogador executa o cliente
Then o cliente deve se conectar ao servidor
And deve exibir a mensagem recebida
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

## História 2 — Cadastrar perguntas do quiz

**Como** sistema,  
**eu quero** manter um banco de perguntas,  
**para que** o MiniKahoot tenha perguntas disponíveis para uma partida.

### Prioridade

Alta.

### Status

Implementada no domínio, mas ainda não integrada ao servidor.

### Evidência no código

- `BancoDePerguntas.java` carrega 3 perguntas iniciais.
- `Pergunta.java` representa enunciado, alternativas e resposta correta.
- `BancoDePerguntasTest` valida adição, busca, limpeza e imutabilidade.

### Critérios de aceitação

```gherkin
Given que o banco de perguntas foi criado
When o sistema inicializa o quiz
Then deve existir uma lista inicial de perguntas
And cada pergunta deve possuir enunciado, alternativas e resposta correta
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#02 - Banco de perguntas` |
| PR | `PR #02 - Cria domínio de perguntas` |
| Código | `Pergunta.java`, `BancoDePerguntas.java` |
| Teste | `BancoDePerguntasTest`, `PerguntaTest` |
| CI/CD | Jenkins executando `mvn test` |

---

## História 3 — Validar pergunta e resposta correta

**Como** sistema,  
**eu quero** validar perguntas e respostas corretas,  
**para que** não existam perguntas inválidas no quiz.

### Prioridade

Alta.

### Status

Implementada.

### Evidência no código

- `Pergunta` rejeita enunciado nulo ou vazio.
- `Pergunta` rejeita lista de alternativas nula, vazia ou com apenas uma alternativa.
- `Pergunta` rejeita resposta correta fora do intervalo.
- `Pergunta` possui `verificarResposta(int resposta)`.

### Critérios de aceitação

```gherkin
Given que uma pergunta será criada
When o enunciado, as alternativas e a resposta correta forem informados
Then o sistema deve aceitar apenas dados válidos
And deve lançar exceção para dados inválidos
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#03 - Validação de perguntas` |
| PR | `PR #03 - Adiciona validações no domínio` |
| Código | `Pergunta.java` |
| Teste | `PerguntaTest.naoDeveCriarPerguntaComEnunciadoVazio`, `PerguntaTest.naoDeveAceitarRespostaForaDoIntervalo` |
| CI/CD | Build Jenkins aprovado |

---

## História 4 — Calcular pontuação por tempo

**Como** jogador,  
**eu quero** receber pontos de acordo com o tempo de resposta,  
**para que** respostas mais rápidas tenham maior valor.

### Prioridade

Alta.

### Status

Implementada no domínio, mas ainda não integrada ao fluxo do servidor.

### Evidência no código

- `GerenciadorDePontos` calcula `100 * (15 - tempoPercorrido)`.
- `GerenciadorPontosTest` valida pontos iniciais, acúmulo de pontos e resposta rápida.

### Ponto de decisão

A regra atual permite pontuação negativa quando o tempo passa de 15 segundos. O grupo deve decidir se isso será uma penalidade intencional ou se será corrigido.

### Critérios de aceitação

```gherkin
Given que existe um jogador cadastrado
When o sistema adiciona pontos considerando o tempo de resposta
Then a pontuação do jogador deve ser atualizada
And uma resposta mais rápida deve gerar mais pontos que uma resposta lenta
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#04 - Pontuação por tempo` |
| PR | `PR #04 - Implementa gerenciador de pontos` |
| Código | `GerenciadorDePontos.java` |
| Teste | `GerenciadorPontosTest.deveAdicionarPontosCorretamente`, `GerenciadorPontosTest.deveDarMaisPontosParaRespostaRapida` |
| CI/CD | Relatório JUnit publicado no Jenkins |

---

## História 5 — Executar testes automaticamente no Jenkins

**Como** integrante do grupo,  
**eu quero** que os testes sejam executados automaticamente no Jenkins,  
**para que** o grupo identifique falhas antes do merge para a `main`.

### Prioridade

Alta.

### Status

Implementada na configuração.

### Evidência no código

- `Dockerfile.jenkins` instala Maven, Git e Docker CLI.
- `docker-compose.yml` sobe Jenkins nas portas `8080` e `50000`.
- `Jenkinsfile` executa ambiente, build, testes, package, relatórios e artefatos.

### Critérios de aceitação

```gherkin
Given que existe uma alteração na branch Backend
When o Jenkins executa o pipeline
Then o projeto deve ser compilado
And os testes unitários devem ser executados
And os relatórios JUnit devem ser publicados
And o artefato .jar deve ser arquivado
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#05 - Pipeline Jenkins em Docker` |
| PR | `PR #05 - Adiciona Jenkinsfile e Docker` |
| Código | `Jenkinsfile`, `Dockerfile.jenkins`, `docker-compose.yml` |
| Teste | Relatórios JUnit publicados pelo Jenkins |
| CI/CD | Pipeline Jenkins em container Docker |

---

## História 6 — Visualizar ranking da partida

**Como** jogador,  
**eu quero** visualizar um ranking com as pontuações,  
**para que** eu saiba minha colocação na partida.

### Prioridade

Média.

### Status

Não implementada nesta versão atual.

### Observação

O código atual possui pontuação por jogador, mas não possui classe de ranking, método de ordenação ou persistência em arquivo. Para a defesa, apresentar essa história como melhoria futura ou implementar antes da entrega.

### Critérios de aceitação propostos

```gherkin
Given que existem jogadores com pontuação
When a partida termina
Then o sistema deve ordenar os jogadores pela maior pontuação
And deve exibir o ranking final
```

### Rastreabilidade esperada caso seja implementada

| Item | Evidência |
|---|---|
| Issue | `#06 - Ranking de jogadores` |
| PR | `PR #06 - Implementa ranking final` |
| Código | `GerenciadorDePontos.java` ou nova classe `Ranking.java` |
| Teste | `GerenciadorPontosTest.deveGerarRankingOrdenadoPorPontuacao` |
| CI/CD | Jenkins arquivando `.jar` após testes |

---

# 3. Metodologia de desenvolvimento

## Metodologia adotada

Sugestão: **Kanban com ciclos semanais**.

Essa metodologia combina com o estado atual do projeto porque o grupo pode separar as tarefas em pequenas entregas: domínio, socket, testes, documentação e Jenkins.

## Justificativa

O Kanban permite:

- Visualizar tarefas pendentes, em andamento e concluídas.
- Dividir trabalho entre os integrantes.
- Priorizar correções antes de novas funcionalidades.
- Relacionar issues, PRs e testes.
- Validar cada mudança no Jenkins.
- Preparar evidências para a defesa.

---

## Papéis do grupo

| Papel | Responsável | Responsabilidade |
|---|---|---|
| Organização / requisitos | Integrante 1 | Histórias de usuário, issues e documentação |
| Dev domínio | Integrante 2 | `Pergunta`, `BancoDePerguntas` e validações |
| Dev pontuação / testes | Integrante 3 | `GerenciadorDePontos` e testes unitários |
| Dev infraestrutura / CI-CD | Integrante 4 | `Servidor`, `Cliente`, Jenkins, Docker e README |

> Mesmo com papéis definidos, todos devem entender o projeto inteiro para a defesa Q&A.

---

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

## Definition of Ready — DoR

Uma tarefa só pode começar quando:

- Possui descrição clara.
- Possui responsável definido.
- Possui critério de aceitação.
- Está ligada a uma história de usuário ou requisito.
- Possui ideia de teste associado.
- Tem impacto claro no código, teste, documentação ou pipeline.
- Não usa GitHub Actions, conforme restrição da NP2.

---

## Definition of Done — DoD

Uma tarefa só é considerada pronta quando:

- Código implementado.
- Testes unitários criados ou atualizados.
- `mvn test` passando localmente.
- Jenkins em Docker executando com sucesso.
- Relatórios JUnit publicados.
- Artefato `.jar` gerado quando aplicável.
- PR revisado por pelo menos um integrante.
- README atualizado, se necessário.
- Issue vinculada fechada.

---

# 4. Dinâmica de desenvolvimento

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

---

## Padrão de commits

```text
feat: nova funcionalidade
fix: correção
test: teste unitário
docs: documentação
refactor: refatoração
ci: pipeline e automação
```

Exemplos adequados ao código atual:

```bash
git commit -m "test: adiciona testes do banco de perguntas"
git commit -m "refactor: centraliza boas-vindas no ServidorService"
git commit -m "fix: impede pontuacao negativa"
git commit -m "ci: adiciona Jenkins em container Docker"
git commit -m "docs: atualiza README com execucao Maven"
```

---

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

# 5. Uso de Inteligência Artificial

A NP2 exige uma seção obrigatória no README chamada:

```md
## Uso de IA
```

## Modelo atualizado para o README

```md
## Uso de IA

Durante o desenvolvimento do MiniKahoot, o grupo utilizou ferramentas de Inteligência Artificial como apoio para planejamento, revisão de código, organização dos testes, documentação e configuração de CI/CD.

### Modelos utilizados

- ChatGPT
- GitHub Copilot, se aplicável
- Gemini ou Claude, se aplicável

### Finalidades de uso

- Sugestão de melhorias para o código Java.
- Revisão da estrutura Maven.
- Apoio na criação de testes unitários com JUnit 5 e Mockito.
- Apoio na configuração do Jenkins em container Docker.
- Apoio na escrita do README.
- Organização de histórias de usuário e critérios de aceitação.

### Exemplos de prompts utilizados

1. "Revise o código atual do MiniKahoot e atualize os guias conforme a estrutura atual."
2. "Como configurar Jenkins em Docker para um projeto Maven Java 17?"
3. "Quais testes unitários fazem sentido para Pergunta, BancoDePerguntas e GerenciadorDePontos?"
4. "Como documentar o uso de IA na entrega da NP2?"
5. "Como organizar histórias de usuário para uma aplicação cliente/servidor de quiz?"

### Respostas aceitas, ajustadas ou descartadas

- Aceitamos a sugestão de organizar o trabalho em domínio, pontuação, comunicação e CI/CD.
- Ajustamos os guias para refletir o código real, sem apresentar JavaFX ou ranking como implementados.
- Aceitamos a sugestão de Jenkins em Docker porque a NP2 não permite GitHub Actions.
- Ajustamos os testes sugeridos para focar nas classes existentes.
- Descartamos sugestões de deploy em produção, pois o projeto é acadêmico e local.

### O que não foi feito por IA

- Execução final do projeto no computador do grupo.
- Configuração final do Jenkins no ambiente local.
- Validação final do Docker Desktop.
- Commits no repositório.
- Revisão final dos PRs.
- Defesa oral do projeto.
```

---

# 6. Preparação para defesa Q&A

## Perguntas prováveis do professor

| Pergunta | Resposta esperada |
|---|---|
| Qual é o objetivo do projeto? | Demonstrar uma aplicação cliente/servidor em Java usando sockets TCP, com base de domínio para quiz. |
| O cliente é gráfico? | Não nesta versão atual. O cliente atual é via terminal. |
| O servidor envia perguntas? | Ainda não no fluxo atual. As perguntas existem no domínio, mas não estão integradas ao servidor. |
| Onde estão as perguntas? | Em `BancoDePerguntas.java` e `Pergunta.java`. |
| Como a resposta correta é validada? | Pelo método `verificarResposta(int resposta)` da classe `Pergunta`. |
| Como a pontuação é calculada? | Pela fórmula `100 * (15 - tempoPercorrido)` em `GerenciadorDePontos`. |
| A pontuação pode ficar negativa? | Sim, na regra atual. O grupo deve explicar como penalidade ou corrigir antes da entrega. |
| Existe ranking? | Não existe ranking real nesta versão. Existe pontuação por jogador, e ranking pode ser melhoria futura. |
| Por que Jenkins? | Porque a NP2 exige CI/CD sem GitHub Actions. |
| Por que Docker? | Para tornar o Jenkins reproduzível e fácil de subir. |
| Onde está o pipeline? | No `Jenkinsfile`. |
| Onde estão os testes? | Em `src/test/java/br/com/kahoot`. |
| Como rodo os testes? | `mvn test` na pasta onde está o `pom.xml`. |
| Como o Jenkins publica relatórios? | Com `junit 'target/surefire-reports/*.xml'`. |
| Como o Jenkins publica artefato? | Com `archiveArtifacts artifacts: 'target/*.jar'`. |
| O e-mail do Jenkins sempre funciona? | Só se SMTP estiver configurado no Jenkins. |
| Como a IA foi usada? | Como apoio para planejamento, revisão, testes, documentação e CI/CD, com revisão final do grupo. |

---

# 7. Checklist atualizado dos requisitos da NP2

- [ ] Criar pelo menos 5 histórias de usuário.
- [ ] Escrever critérios de aceitação em Given/When/Then.
- [ ] Indicar prioridade de cada história.
- [ ] Indicar status real de cada história.
- [ ] Não apresentar JavaFX como implementado nesta versão.
- [ ] Não apresentar ranking persistido como implementado nesta versão.
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
- [ ] Demonstrar Jenkins rodando em container.
- [ ] Demonstrar Jenkins rodando pipeline.
- [ ] Demonstrar relatórios JUnit no Jenkins.
- [ ] Demonstrar artefato `.jar` publicado.
- [ ] Atualizar README com estrutura Maven real.
- [ ] Rodar `mvn clean test` antes da entrega.
- [ ] Preparar todos para defesa Q&A.
