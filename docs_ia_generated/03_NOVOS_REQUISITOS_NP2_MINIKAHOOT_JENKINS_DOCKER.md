# MiniKahoot — Guia 03: Novos Requisitos da NP2 com Jenkins em Container Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**CI/CD adotado:** Jenkins em container Docker  
**Objetivo:** organizar as entregas novas exigidas pela NP2: histórias de usuário, metodologia, dinâmica de desenvolvimento, uso de IA, refactoring, CI/CD sem GitHub Actions e preparação para defesa Q&A.

---

## 1. O que mudou na NP2?

Além de manter a aplicação funcional, testes, versionamento, README e CI/CD, a NP2 adiciona novas exigências de maturidade de processo:

1. Histórias de usuário.
2. Metodologia de desenvolvimento.
3. Dinâmica real de desenvolvimento.
4. Uso transparente de IA.
5. Defesa em formato Q&A.
6. Evidências concretas: commits, PRs, issues, testes e pipeline.
7. Refactorings aplicados e justificados.
8. CI/CD sem GitHub Actions.
9. Pelo menos 1 job por integrante.
10. Relatórios de teste entregues via CI/CD.

A solução proposta usa Jenkins em container Docker para atender à restrição de não usar GitHub Actions e ainda tornar o ambiente reproduzível.

---

# 2. Histórias de Usuário

O trabalho pede no mínimo 5 histórias no formato:

```text
Como <perfil>, eu quero <ação> para que <benefício>.
```

Cada história deve ter:

- Critérios de aceitação.
- Prioridade.
- Status final.
- Rastreabilidade: história → issue/PR → teste automatizado.

---

## História 1 — Entrar no jogo

**Como** jogador,  
**eu quero** me conectar ao servidor do MiniKahoot,  
**para que** eu possa participar da partida.

### Prioridade

Alta.

### Status

Entregue ou parcial, dependendo do estado atual do cliente/servidor.

### Critérios de aceitação

```gherkin
Given que o servidor está em execução
When o jogador inicia o cliente
Then o sistema deve estabelecer conexão com o servidor
And o jogador deve receber uma mensagem de boas-vindas
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#01 - Conexão do jogador ao servidor` |
| PR | `PR #01 - Implementa conexão cliente-servidor` |
| Teste | `ServidorServiceTest.deveEnviarMensagemDeBoasVindas` |
| CI/CD | Relatório JUnit publicado no Jenkins |

---

## História 2 — Responder pergunta

**Como** jogador,  
**eu quero** receber uma pergunta com alternativas,  
**para que** eu possa escolher uma resposta durante a partida.

### Prioridade

Alta.

### Status

Entregue ou parcial.

### Critérios de aceitação

```gherkin
Given que o jogador está conectado
When o servidor envia uma pergunta
Then o cliente deve exibir o enunciado
And deve exibir as alternativas disponíveis
And deve permitir que o jogador escolha uma alternativa
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#02 - Envio e exibição de perguntas` |
| PR | `PR #02 - Implementa fluxo de perguntas` |
| Teste | `PerguntaTest.deveFormatarPerguntaParaEnvio` |
| CI/CD | Jenkins executando `mvn test` |

---

## História 3 — Validar resposta

**Como** sistema,  
**eu quero** verificar se a resposta enviada pelo jogador está correta,  
**para que** a pontuação seja calculada corretamente.

### Prioridade

Alta.

### Status

Entregue ou parcial.

### Critérios de aceitação

```gherkin
Given que uma pergunta possui uma alternativa correta
When o jogador envia uma resposta
Then o sistema deve comparar a resposta com o gabarito
And deve identificar acerto ou erro
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#03 - Validação de respostas` |
| PR | `PR #03 - Implementa validação de respostas` |
| Teste | `PerguntaTest.deveIdentificarRespostaCorreta` |
| CI/CD | Build Jenkins aprovado |

---

## História 4 — Pontuar jogador

**Como** jogador,  
**eu quero** receber pontos ao acertar uma pergunta,  
**para que** meu desempenho seja contabilizado.

### Prioridade

Alta.

### Status

Entregue.

### Critérios de aceitação

```gherkin
Given que o jogador respondeu corretamente
When o sistema processa a resposta
Then a pontuação do jogador deve aumentar
And a nova pontuação deve ficar disponível no ranking
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#04 - Sistema de pontuação` |
| PR | `PR #04 - Implementa pontuação dos jogadores` |
| Teste | `GerenciadorPontosTest.deveAdicionarPontosQuandoJogadorAcerta` |
| CI/CD | Relatório JUnit no Jenkins |

---

## História 5 — Visualizar ranking

**Como** jogador,  
**eu quero** visualizar o ranking da partida,  
**para que** eu saiba minha colocação em relação aos outros jogadores.

### Prioridade

Média.

### Status

Parcial ou entregue, dependendo da implementação.

### Critérios de aceitação

```gherkin
Given que existem jogadores com pontuação
When a partida termina
Then o sistema deve exibir o ranking ordenado
And o jogador com maior pontuação deve aparecer primeiro
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#05 - Ranking de jogadores` |
| PR | `PR #05 - Implementa ranking final` |
| Teste | `GerenciadorPontosTest.deveOrdenarRankingPorMaiorPontuacao` |
| CI/CD | Jenkins arquivando `.jar` após testes |

---

## História 6 — Executar testes automaticamente no Jenkins

**Como** integrante do grupo,  
**eu quero** que os testes sejam executados automaticamente no Jenkins,  
**para que** o grupo identifique falhas antes do merge na `main`.

### Prioridade

Alta.

### Status

A entregar na NP2.

### Critérios de aceitação

```gherkin
Given que existe uma atualização na branch Backend
When o Jenkins executa o pipeline em container Docker
Then o sistema deve rodar build e testes
And deve publicar relatórios JUnit
And deve gerar o artefato .jar
And deve gerar evidência para a defesa
```

### Rastreabilidade

| Item | Evidência |
|---|---|
| Issue | `#06 - Configurar pipeline Jenkins em Docker` |
| PR | `PR #06 - Adiciona Jenkinsfile e Docker` |
| Teste | Relatórios JUnit publicados pelo Jenkins |
| CI/CD | `Dockerfile.jenkins`, `docker-compose.yml` e `Jenkinsfile` |

---

# 3. Metodologia de Desenvolvimento

## Metodologia adotada

Sugestão: **Kanban com ciclos semanais**.

Essa escolha é adequada porque o grupo possui um projeto acadêmico com entregas incrementais, horários diferentes entre integrantes e necessidade de acompanhar tarefas por status.

## Justificativa

O Kanban permite:

- Visualizar tarefas pendentes, em andamento e concluídas.
- Reorganizar prioridades conforme erros aparecem.
- Dividir trabalho entre integrantes.
- Manter evidências por issues e PRs.
- Trabalhar bem mesmo sem sprints formais longas.
- Integrar o fluxo com o Jenkins.
- Validar tarefas automaticamente no pipeline.

---

## Papéis do grupo

| Papel | Responsável | Responsabilidade |
|---|---|---|
| PO / organização | Integrante 1 | Organizar requisitos e histórias |
| Dev domínio | Integrante 2 | Perguntas, respostas e pontuação |
| Dev infraestrutura | Integrante 3 | Servidor, cliente e sockets |
| QA / CI-CD | Integrante 4 | Testes, Jenkins, Docker e evidências |

> Mesmo com papéis definidos, todos devem entender o sistema inteiro para a defesa Q&A.

---

## Ferramenta de acompanhamento

Sugestões:

- GitHub Issues.
- GitHub Projects.
- Trello.
- Notion.
- Jenkins para evidências de pipeline.
- Docker Desktop para executar Jenkins em container.

Como o repositório está no GitHub, a opção mais simples é:

```text
GitHub Issues + Pull Requests + Jenkins em Docker
```

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

- Tem descrição clara.
- Tem responsável definido.
- Tem critério de aceitação.
- Está conectada a uma história de usuário ou requisito.
- Tem ideia de teste associado.
- Tem impacto claro no código, teste, documentação ou pipeline.
- Não conflita com a restrição de não usar GitHub Actions.

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

## Métricas simples

O grupo pode apresentar métricas simples, como:

| Métrica | Como medir |
|---|---|
| Issues fechadas | Quantidade de issues concluídas |
| PRs revisados | Quantidade de PRs com comentários |
| Testes adicionados | Quantidade de novos testes unitários |
| Commits por integrante | Histórico do Git |
| Pipeline Jenkins | Execuções com sucesso/falha |
| Lead time | Tempo entre abrir e fechar uma issue |
| Builds bem-sucedidos | Histórico do Jenkins |
| Artefatos gerados | Arquivos `.jar` arquivados pelo Jenkins |

---

# 4. Dinâmica de Desenvolvimento

## Como o trabalho aconteceu

Modelo de relato honesto:

> O grupo trabalhou inicialmente com foco em fazer a aplicação funcionar. Na NP2, reorganizamos o projeto para dar mais maturidade ao processo, adicionando testes, pipeline Jenkins em container Docker, revisão por PRs, histórias de usuário e documentação do uso de IA.

---

## Divisão de tarefas

| Integrante | Foco |
|---|---|
| Integrante 1 | Histórias de usuário e perguntas |
| Integrante 2 | Pontuação, ranking e testes |
| Integrante 3 | Servidor, protocolo e cliente |
| Integrante 4 | Jenkins, Docker, README, IA e evidências |

---

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

Exemplos:

```bash
git commit -m "test: adiciona testes de ranking"
git commit -m "ci: adiciona Jenkinsfile"
git commit -m "ci: adiciona Dockerfile do Jenkins"
git commit -m "ci: adiciona docker compose para Jenkins"
git commit -m "docs: documenta uso de IA no README"
```

---

## Code review

Cada PR deve ter:

- Descrição do que foi alterado.
- Issue relacionada.
- Evidência de teste.
- Comentário de pelo menos um colega.
- Jenkins passando.
- Relatórios JUnit disponíveis.
- Artefato `.jar`, quando aplicável.

---

## Bloqueios possíveis e solução

| Bloqueio | Solução |
|---|---|
| Maven rodando na pasta errada | Entrar na pasta onde está o `pom.xml` |
| Java incompatível | Padronizar Java 17 |
| Mockito com `Socket` | Refatorar para interface de conexão |
| Falta de testes relevantes | Criar testes ligados às regras do quiz |
| CI/CD não pode usar GitHub Actions | Usar Jenkins |
| Jenkins não sobe | Verificar Docker Desktop e `docker compose up` |
| Jenkins não encontra Maven | Instalar Maven no `Dockerfile.jenkins` |
| Jenkins não envia e-mail | Configurar SMTP ou documentar limitação |
| Container perde configuração | Usar volume `jenkins_home` |
| Contribuição desigual | Dividir stages, testes, issues e commits |

---

## Lições aprendidas

Sugestões para colocar no README ou apresentação:

- É melhor configurar CI/CD antes das grandes mudanças.
- Testar regra de negócio é mais útil do que testar métodos triviais.
- Separar socket da regra de jogo facilita testes.
- Commits pequenos ajudam a explicar o histórico.
- IA ajuda, mas o grupo precisa entender e revisar tudo.
- Usar Java 17 em todos os ambientes evita erros de compatibilidade.
- Jenkins deixa o fluxo de CI/CD mais visível para defesa.
- Docker torna o ambiente de CI/CD mais reproduzível.

---

# 5. Uso de Inteligência Artificial

A NP2 exige uma seção obrigatória no README chamada:

```md
## Uso de IA
```

---

## Modelo de seção para o README

```md
## Uso de IA

Durante o desenvolvimento do MiniKahoot, o grupo utilizou ferramentas de Inteligência Artificial de forma transparente para apoio em brainstorming, refatoração, testes, documentação, depuração e planejamento do CI/CD.

### Modelos utilizados

- ChatGPT
- GitHub Copilot, se aplicável
- Gemini ou Claude, se aplicável

### Finalidades de uso

- Sugestão de melhorias no código.
- Organização das tarefas por integrante.
- Criação de ideias de testes unitários.
- Apoio na configuração do pipeline Jenkins.
- Apoio na criação de Dockerfile e docker-compose para Jenkins.
- Ajuda na escrita da documentação.
- Debug de erros do Maven, Java e Mockito.

### Exemplos de prompts utilizados

1. "Estou fazendo esse código MiniKahoot em 4 pessoas. Considerando o código atual, descreva melhorias e testes unitários e divida em 4 partes."
2. "Como configurar CI/CD para projeto Maven sem usar GitHub Actions?"
3. "É possível utilizar Jenkins no lugar do CircleCI?"
4. "Como usar Jenkins em container Docker com Dockerfile e Jenkinsfile?"
5. "Por que o Mockito não consegue mockar Socket no Java 22?"

### Respostas aceitas, ajustadas ou descartadas

- Aceitamos a divisão inicial de tarefas, mas ajustamos para as branches reais `main` e `Backend`.
- Aceitamos a ideia de CI/CD, mas trocamos GitHub Actions por Jenkins porque a especificação da NP2 não permite GitHub Actions.
- Ajustamos a solução para rodar Jenkins em container Docker, deixando o ambiente mais reproduzível.
- Ajustamos os testes sugeridos para focar no domínio do MiniKahoot.
- Descartamos sugestões que não se aplicavam ao projeto, como deploy real em produção.

### Dinâmica de uso

A IA foi usada como apoio de revisão e planejamento. As decisões finais, implementação, commits, testes e validações foram feitos pelo grupo.

### O que não foi feito por IA

- Execução local do projeto.
- Configuração final do Jenkins no ambiente do grupo.
- Validação do Docker no computador do grupo.
- Decisão final sobre arquitetura.
- Validação dos testes no ambiente do grupo.
- Commits no repositório.
- Revisões finais dos PRs.
```

---

# 6. Preparação para defesa Q&A

A apresentação será em formato de perguntas e respostas. Portanto, todos devem saber responder sobre o projeto inteiro.

## O que cada integrante precisa saber

- Como o servidor funciona.
- Como o cliente se conecta.
- Como as perguntas são representadas.
- Como a pontuação é calculada.
- Como os testes rodam.
- Onde está o `Dockerfile.jenkins`.
- Onde está o `docker-compose.yml`.
- Como subir o Jenkins em Docker.
- Onde está o `Jenkinsfile`.
- Como o Jenkins executa o pipeline.
- Por que Jenkins foi usado.
- Por que Docker foi usado.
- Onde estão os relatórios JUnit.
- Onde está o artefato `.jar`.
- Quais refactorings foram feitos.
- Como a IA foi usada.
- Quais histórias de usuário guiaram o projeto.

---

## Perguntas prováveis do professor

| Pergunta | Resposta esperada |
|---|---|
| Por que vocês não usaram GitHub Actions? | Porque a especificação da NP2 proíbe GitHub Actions. |
| Qual CI/CD vocês usaram? | Jenkins. |
| Por que Jenkins em container Docker? | Para tornar o ambiente reproduzível e fácil de subir. |
| Onde está a configuração do Jenkins em Docker? | Em `Dockerfile.jenkins` e `docker-compose.yml`. |
| Onde está o pipeline? | No `Jenkinsfile`. |
| Onde estão os testes? | Em `src/test/java/...`. |
| Como rodo os testes? | `mvn test` na pasta do `pom.xml`. |
| Como o Jenkins publica relatórios? | Com `junit 'target/surefire-reports/*.xml'`. |
| Como o Jenkins publica artefato? | Com `archiveArtifacts artifacts: 'target/*.jar'`. |
| Qual refactoring foi mais importante? | Separar comunicação via socket da regra de negócio. |
| Como cada integrante contribuiu? | Commits, issues, PRs e stages individuais no pipeline. |
| Como vocês usaram IA? | De forma declarada no README, com prompts e ajustes documentados. |

---

# 7. Checklist dos novos requisitos da NP2

- [ ] Criar pelo menos 5 histórias de usuário.
- [ ] Escrever critérios de aceitação em Given/When/Then.
- [ ] Indicar prioridade de cada história.
- [ ] Indicar status final de cada história.
- [ ] Criar rastreabilidade história → issue/PR → teste.
- [ ] Explicar metodologia adotada.
- [ ] Definir papéis do grupo.
- [ ] Registrar cadência/ciclos de trabalho.
- [ ] Definir DoR e DoD.
- [ ] Levantar métricas simples.
- [ ] Explicar dinâmica real do grupo.
- [ ] Documentar fluxo de branches.
- [ ] Documentar code review.
- [ ] Documentar conflitos e bloqueios.
- [ ] Documentar lições aprendidas.
- [ ] Criar seção `Uso de IA` no README.
- [ ] Listar modelos usados.
- [ ] Listar pelo menos 3 prompts reais.
- [ ] Explicar respostas aceitas, ajustadas e descartadas.
- [ ] Explicar o que não foi feito por IA.
- [ ] Criar `Dockerfile.jenkins`.
- [ ] Criar `docker-compose.yml`.
- [ ] Criar `Jenkinsfile`.
- [ ] Demonstrar Jenkins rodando em container.
- [ ] Demonstrar Jenkins rodando pipeline.
- [ ] Demonstrar relatórios JUnit no Jenkins.
- [ ] Demonstrar artefato `.jar` publicado.
- [ ] Preparar todos para defesa Q&A.