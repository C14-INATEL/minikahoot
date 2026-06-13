# MiniKahoot - Guia 06: Melhorias Implementadas na Sessao Backend

**Projeto:** MiniKahoot  
**Disciplina:** C14 - Engenharia de Software  
**CI/CD adotado:** Jenkins em container Docker  
**Foco desta sessao:** consolidar melhorias funcionais, testes, documentacao e ajustes de pipeline no backend do projeto.  
**Observacao:** este guia resume apenas as melhorias implementadas nesta sessao. A parte de merge entre branches foi intencionalmente omitida.

---

## 1. Objetivo deste guia

Este guia registra de forma organizada as melhorias feitas na sessao mais recente de desenvolvimento do MiniKahoot. O objetivo e deixar claro:

- o que foi alterado no jogo;
- quais arquivos foram impactados;
- quais testes e documentacoes foram ajustados;
- quais prompts ou solicitacoes originaram cada evolucao;
- quais evidencias podem ser usadas na defesa.

---

## 2. Resumo executivo das melhorias

Nesta sessao, o projeto evoluiu em sete frentes principais:

1. Atualizacao do `README.md` para refletir as historias de usuario e os guias de apoio com IA.
2. Expansao do fluxo da partida de 1 pergunta para 5 perguntas por sessao.
3. Inclusao de nome do jogador antes do quiz e ranking geral persistido em arquivo.
4. Melhoria da experiencia do cliente no terminal, com exibicao mais amigavel das alternativas.
5. Expansao do banco de perguntas para 30 perguntas base.
6. Sorteio de 5 perguntas aleatorias por partida, sem repeticao na mesma sessao.
7. Ajustes de documentacao e pipeline, incluindo limpeza do `README.md` e corpo de e-mail do `Jenkinsfile`.

---

## 3. Estado final do projeto apos a sessao

Ao final desta sessao, o MiniKahoot passou a ter:

- cliente de terminal com entrada de nome do jogador;
- 30 perguntas cadastradas no banco;
- sorteio de 5 perguntas por partida;
- resposta enviada pelo cliente com prompt mais amigavel;
- pontuacao acumulada ao longo da partida;
- ranking geral persistido no arquivo `ranking_geral.txt`;
- melhor pontuacao de cada jogador preservada entre execucoes locais;
- `README.md` e `USER_STORIES.md` alinhados com o comportamento real do sistema;
- testes atualizados para cobrir ranking persistente e sorteio de perguntas;
- `Jenkinsfile` com corpo de e-mail mais seguro e mais legivel.

---

## 4. Melhoria 1 - Atualizacao do README com historias de usuario e apoio de IA

### Objetivo

Registrar no `README.md` que o projeto passou a contar com:

- historias de usuario em `USER_STORIES.md`;
- guias de apoio em `docs_ia_generated/`;
- uso de IA como suporte revisado pela equipe.

### Arquivo impactado

```text
README.md
```

### Resultado implementado

- Inclusao de `USER_STORIES.md` e `docs_ia_generated/` na estrutura do projeto.
- Criacao de uma secao explicando o papel das historias de usuario.
- Registro do uso de IA como apoio tecnico, com revisao humana da equipe.

### Evidencia esperada

- `README.md` mostrando a estrutura do projeto e a documentacao de apoio.

---

## 5. Melhoria 2 - Partida com 5 perguntas por sessao

### Objetivo

Fazer o jogo deixar de encerrar apos uma unica pergunta e passar a executar uma partida completa com 5 perguntas.

### Arquivos impactados

```text
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
README.md
```

### Resultado implementado

- O `ServidorService` passou a iterar por varias perguntas.
- O cliente passou a receber o ciclo `PERGUNTA -> ALT -> RESPONDA -> RESULTADO -> PONTOS` cinco vezes.
- O encerramento com `FIM` passou a acontecer apenas apos a ultima pergunta.

### Evidencia esperada

- Testes de `ServidorServiceTest` cobrindo cinco perguntas.
- Execucao manual mostrando cinco rodadas antes do `FIM`.

---

## 6. Melhoria 3 - Nome do jogador e ranking geral persistente

### Objetivo

Adicionar identidade ao jogador e manter um ranking geral entre execucoes locais.

### Arquivos impactados

```text
src/main/java/br/com/kahoot/Cliente.java
src/main/java/br/com/kahoot/RankingGeral.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/RankingGeralTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
README.md
USER_STORIES.md
.gitignore
```

### Resultado implementado

- O servidor envia `NOME` antes de iniciar o quiz.
- O cliente solicita o nome no terminal e o envia ao servidor.
- A classe `RankingGeral` foi criada para persistir a melhor pontuacao por jogador.
- O arquivo `ranking_geral.txt` passou a armazenar esse ranking.
- O ranking final passou a ser enviado ao cliente no protocolo com:

```text
RANKING_INICIO
RANKING|posicao|jogador|pontos
RANKING_FIM
```

- O servidor tambem exibe o ranking no proprio terminal.

### Evidencia esperada

- Classe `RankingGeral.java`.
- Teste `RankingGeralTest`.
- `README.md` documentando `ranking_geral.txt`.
- `USER_STORIES.md` atualizada na `US-004`.

---

## 7. Melhoria 4 - Experiencia do cliente no terminal

### Objetivo

Melhorar a apresentacao das mensagens para o usuario final, sem alterar a estrutura do protocolo entre cliente e servidor.

### Arquivo impactado

```text
src/main/java/br/com/kahoot/Cliente.java
```

### Resultado implementado

- As alternativas deixaram de aparecer com o prefixo cru do protocolo.
- O cliente passou a mostrar:

```text
1) alternativa
2) alternativa
3) alternativa
4) alternativa
```

- O prompt de resposta foi alterado para:

```text
Digite sua resposta (ex: 2):
```

- O cliente tambem passou a formatar melhor mensagens de boas-vindas, perguntas, pontos e ranking.

### Evidencia esperada

- Execucao manual do cliente mostrando a interface textual mais amigavel.

---

## 8. Melhoria 5 - Banco com 30 perguntas

### Objetivo

Ampliar o conteudo do quiz para deixar as partidas menos repetitivas e mais criveis para apresentacao.

### Arquivo impactado

```text
src/main/java/br/com/kahoot/BancoDePerguntas.java
```

### Resultado implementado

- O banco passou de 5 perguntas para 30 perguntas base.
- As perguntas cobrem temas do proprio projeto e de Java/Maven/Jenkins, coerentes com a proposta academica.

### Evidencia esperada

- `BancoDePerguntasTest` validando o total de 30 perguntas.

---

## 9. Melhoria 6 - Sorteio de 5 perguntas aleatorias por partida

### Objetivo

Evitar que todas as partidas sigam sempre a mesma ordem e reforcar a ideia de sessao unica de jogo.

### Arquivos impactados

```text
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
README.md
USER_STORIES.md
```

### Resultado implementado

- O `BancoDePerguntas` ganhou o metodo `obterPerguntasAleatorias(int quantidade)`.
- O `ServidorService` passou a solicitar 5 perguntas sorteadas por sessao.
- As perguntas nao se repetem dentro da mesma partida.
- Os testes do servico foram ajustados para nao depender da ordem aleatoria real.

### Evidencia esperada

- Teste cobrindo sorteio de 5 perguntas sem repeticao.
- `README.md` documentando 30 perguntas base e 5 por sessao.

---

## 10. Melhoria 7 - Ajustes de README e Jenkinsfile

### Objetivo

Corrigir pequenos pontos de consistencia e profissionalismo na documentacao e no pipeline.

### Arquivos impactados

```text
README.md
Jenkinsfile
```

### Resultado implementado

- Remocao de uma duplicacao pequena na descricao do `Cliente` no `README.md`.
- Ajuste do corpo do e-mail de sucesso no `Jenkinsfile` para usar `\n` explicitamente:

```groovy
body: "O pipeline do MiniKahoot foi executado com sucesso.\nBuild, testes, package e publicacao de artefatos passaram."
```

### Evidencia esperada

- `README.md` sem descricao repetida.
- `Jenkinsfile` com corpo de e-mail seguro no Groovy.

---

## 11. Arquivos criados nesta sessao

```text
src/main/java/br/com/kahoot/RankingGeral.java
src/test/java/br/com/kahoot/RankingGeralTest.java
USER_STORIES.md
```

> Observacao: `USER_STORIES.md` passou a ser tratado como artefato oficial do projeto nesta sessao.

---

## 12. Arquivos alterados nesta sessao

```text
README.md
.gitignore
Jenkinsfile
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/main/java/br/com/kahoot/Cliente.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

---

## 13. Testes e validacao realizados

Durante a sessao, o projeto foi validado com Maven apos as principais mudancas.

### Comandos executados

```bash
mvn test
mvn clean test
mvn clean package
```

### Resultado observado

- Build com `BUILD SUCCESS`.
- Suite de testes crescendo ao longo da sessao ate chegar a mais de 40 testes aprovados.
- Cobertura funcional ampliada para banco de perguntas, protocolo, ranking persistente e sorteio de perguntas.

---

## 14. Prompts e solicitacoes que originaram as melhorias

Esta secao resume os pedidos que motivaram o codigo e a documentacao gerados nesta sessao.

### Prompt 1 - Atualizacao da documentacao principal

```text
Atualize o README com base nas historias de usuario e nos guias de apoio gerados por IA, deixando coerente com o que ja foi feito no projeto.
```

### Prompt 2 - Ampliacao da partida

```text
Esta facil de fazer o jogo ter 5 perguntas?
Pode fazer esses 5 topicos de melhorias.
```

### Prompt 3 - Nome do jogador e ranking persistido

```text
Tem como antes de iniciar as perguntas perguntar o nome do jogador e ao final da execucao o servidor mostrar um ranking geral com as melhores pontuacoes?
2
```

> O `2` acima correspondeu a escolha da versao com ranking geral salvo em arquivo.

### Prompt 4 - Melhoria da interface textual do cliente

```text
Vamos mudar os textos de interacao com o usuario:
tire o ALT das alternativas;
Digite sua resposta (ex: 2):
```

### Prompt 5 - Atualizacao da documentacao funcional

```text
Pode atualizar o README e tambem caso necessario as historias de usuario.
```

### Prompt 6 - Expansao do banco e sorteio de perguntas

```text
Tem como ter mais perguntas no banco, umas 30 e a cada sessao vir 5 aleatorias?
Pode implementar.
```

### Prompt 7 - Ajustes finos de qualidade

```text
README tem uma duplicacao pequena.
Jenkinsfile ainda tem risco no body do e-mail.
```

---

## 15. Commits sugeridos para as melhorias desta sessao

Uma sequencia coerente de commits para representar esta sessao seria:

```bash
git add src/main/java/br/com/kahoot/BancoDePerguntas.java src/main/java/br/com/kahoot/ServidorService.java
git commit -m "feat: adiciona banco com 30 perguntas e sorteio de 5 por partida"

git add src/main/java/br/com/kahoot/RankingGeral.java src/main/java/br/com/kahoot/Cliente.java .gitignore
git commit -m "feat: adiciona nome do jogador e ranking geral persistente"

git add src/test/java/br/com/kahoot/BancoDePerguntasTest.java src/test/java/br/com/kahoot/ServidorServiceTest.java src/test/java/br/com/kahoot/RankingGeralTest.java
git commit -m "test: atualiza cobertura para sorteio e ranking persistente"

git add README.md USER_STORIES.md Jenkinsfile
git commit -m "docs: atualiza README, historias e ajustes do pipeline"
```

---

## 16. Como explicar essas melhorias na defesa

### Sobre o jogo

> O MiniKahoot evoluiu de uma prova de conceito simples para uma sessao completa de quiz. Agora o sistema pede o nome do jogador, sorteia 5 perguntas entre 30 cadastradas, acumula a pontuacao durante a partida e mostra um ranking geral persistido em arquivo.

### Sobre a experiencia do usuario

> Tambem melhoramos a interface textual do cliente, removendo detalhes tecnicos do protocolo da exibicao final e tornando o fluxo mais natural para o jogador.

### Sobre qualidade e processo

> Alem do codigo, atualizamos README, historias de usuario, testes e Jenkinsfile para manter coerencia entre implementacao, documentacao e pipeline.

---

## 17. Melhorias futuras apos esta sessao

Mesmo com os avancos, ainda existem evolucoes possiveis:

- medir o tempo real de resposta do jogador;
- suportar multiplos clientes simultaneos;
- categorizar perguntas por tema ou dificuldade;
- limitar ou paginar o ranking geral;
- melhorar os testes da interface JavaFX para evitar ruido no console;
- permitir configuracao externa do total de perguntas por sessao.

---

## 18. Conclusao

Esta sessao consolidou o MiniKahoot em um estado muito mais maduro para apresentacao:

- o fluxo do jogo ficou mais completo;
- o banco de perguntas ficou mais robusto;
- o ranking passou a ter valor pratico entre execucoes;
- a documentacao ficou coerente com o comportamento real do sistema;
- a validacao automatizada continuou funcionando com Maven e testes unitarios.

Esse conjunto de melhorias fortalece tanto a demonstracao funcional quanto a defesa tecnica do projeto.
