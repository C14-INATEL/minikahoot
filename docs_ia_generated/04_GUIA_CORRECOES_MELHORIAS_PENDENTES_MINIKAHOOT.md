# MiniKahoot — Guia 04: Correções e Melhorias Pendentes do Código Atual

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**Base de revisão:** código atual do `minikahoot_v2.zip`  
**Objetivo:** aplicar as correções pendentes identificadas na revisão do Guia 02 sem quebrar o projeto, mantendo coerência com a estrutura atual.

---

## 1. Objetivo deste guia

Este guia organiza as melhorias pendentes do projeto MiniKahoot em **4 partes**, cada uma com foco técnico, arquivos prováveis, passos de implementação, testes esperados e commits recomendados.

As melhorias tratadas aqui são:

1. Corrigir o cliente para ler todas as mensagens enviadas pelo servidor.
2. Implementar ranking de jogadores.
3. Fazer o servidor receber resposta, validar acerto/erro e aplicar pontuação.
4. Limpar relatórios antigos e validar os testes com `mvn clean test`.

A ideia é evoluir o código atual de forma incremental, evitando alterações grandes em um único commit.

---

## 2. Estrutura atual considerada

A estrutura atual do projeto considera principalmente os seguintes arquivos:

```text
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/main/java/br/com/kahoot/Cliente.java
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/main/java/br/com/kahoot/Pergunta.java
src/main/java/br/com/kahoot/Servidor.java
src/main/java/br/com/kahoot/ServidorService.java

src/test/java/br/com/kahoot/BancoDePerguntasTest.java
src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
src/test/java/br/com/kahoot/PerguntaTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java

Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
pom.xml
```

A estrutura está coerente com Java/Maven, testes JUnit e Jenkins em container Docker.

---

## 3. Ordem recomendada de execução

Para evitar quebrar o projeto, seguir esta ordem:

```text
1. Corrigir Cliente.java para ler todas as mensagens do servidor.
2. Criar ranking no GerenciadorDePontos.
3. Integrar resposta, validação e pontuação no ServidorService.
4. Rodar mvn clean test e limpar evidências antigas da pasta target.
5. Atualizar README ou guia de execução, se necessário.
6. Validar Jenkins.
7. Fazer merge para main apenas após build e testes passarem.
```

---

# PARTE 1 — Corrigir Cliente.java para ler o protocolo completo

**Responsável:** Integrante 1  
**Foco:** corrigir a experiência do jogador no cliente.

## Problema atual

O servidor envia várias linhas para o cliente, por exemplo:

```text
BEM_VINDO
PERGUNTA: ...
ALT: ...
ALT: ...
FIM_PERGUNTA
FIM
```

Porém, o `Cliente.java` lê apenas uma linha com `readLine()`. Assim, o cliente tende a exibir somente a primeira mensagem, normalmente `BEM_VINDO`, e não mostra a pergunta completa.

## Arquivo principal

```text
src/main/java/br/com/kahoot/Cliente.java
```

## Correção sugerida

Alterar o cliente para continuar lendo mensagens até receber `FIM`.

Exemplo de lógica:

```java
String linha;
while ((linha = in.readLine()) != null) {
    System.out.println("Servidor disse: " + linha);

    if (linha.equals("FIM")) {
        break;
    }
}
```

## Melhorias recomendadas

- Ler todas as mensagens do servidor.
- Encerrar o loop quando receber `FIM`.
- Exibir perguntas e alternativas no terminal.
- Não travar caso o servidor encerre a conexão.
- Manter a implementação simples, sem alterar ainda a regra de pontuação.

## Teste manual esperado

1. Rodar o servidor.
2. Rodar o cliente.
3. Verificar se o cliente exibe:
   - boas-vindas;
   - pergunta;
   - alternativas;
   - fim da pergunta;
   - fim da comunicação.

## Commits recomendados

### Commit 1 — Corrigir leitura do cliente

```bash
git add src/main/java/br/com/kahoot/Cliente.java
git commit -m "fix: faz cliente ler todas as mensagens do servidor"
```

### Commit 2 — Documentar teste manual do cliente, se alterar README

```bash
git add README.md
git commit -m "docs: documenta fluxo de execucao do cliente"
```

## Checklist da Parte 1

- [ ] Cliente lê mais de uma linha.
- [ ] Cliente encerra ao receber `FIM`.
- [ ] Cliente mostra pergunta e alternativas.
- [ ] Servidor continua funcionando.
- [ ] Código compila.

---

# PARTE 2 — Implementar ranking no GerenciadorDePontos

**Responsável:** Integrante 2  
**Foco:** concluir a melhoria de ranking prevista no Guia 02.

## Problema atual

O projeto possui `GerenciadorDePontos`, mas ainda não há um método claro para retornar o ranking ordenado dos jogadores.

O ranking foi citado como melhoria no Guia 02, então é importante implementar essa parte ou declarar explicitamente como melhoria futura. A recomendação é implementar, pois a alteração é pequena e melhora a coerência do projeto.

## Arquivos principais

```text
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
```

## Correção sugerida

Criar um método que retorne os jogadores ordenados por maior pontuação.

Exemplo de implementação possível:

```java
public String[] obterRanking() {
    String[] ranking = Arrays.copyOf(jogadores, jogadores.length);

    Arrays.sort(ranking, (jogador1, jogador2) -> {
        int pontos1 = obterPontosDoJogador(jogador1);
        int pontos2 = obterPontosDoJogador(jogador2);
        return Integer.compare(pontos2, pontos1);
    });

    return ranking;
}
```

> Observação: se a estrutura interna não permitir buscar pontos pelo nome diretamente, pode ser melhor criar uma classe simples ou retornar uma lista formatada. O importante é não quebrar os testes já existentes.

## Alternativa simples

Se o `GerenciadorDePontos` trabalha com índice do jogador, pode ser criado um método que retorna texto formatado:

```java
public String formatarRanking() {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < jogadores.length; i++) {
        sb.append(i + 1)
          .append(". ")
          .append(jogadores[i])
          .append(" - ")
          .append(pontos[i])
          .append(" pontos")
          .append(System.lineSeparator());
    }

    return sb.toString();
}
```

Essa alternativa é mais simples, mas não ordena. Para atender melhor ao Guia 02, o ideal é ordenar por pontuação.

## Testes unitários recomendados

Adicionar testes como:

```text
deveRetornarRankingOrdenadoPorMaiorPontuacao
naoDevePermitirAlterarRankingExternamente
```

Exemplo de intenção do teste:

```java
@Test
void deveRetornarRankingOrdenadoPorMaiorPontuacao() {
    GerenciadorDePontos gp = new GerenciadorDePontos(new String[]{"Ana", "Bruno", "Carlos"}, 3);

    gp.adicionarPontos(0, 10);
    gp.adicionarPontos(1, 30);
    gp.adicionarPontos(2, 20);

    String[] ranking = gp.obterRanking();

    assertEquals("Bruno", ranking[0]);
    assertEquals("Carlos", ranking[1]);
    assertEquals("Ana", ranking[2]);
}
```

## Commits recomendados

### Commit 1 — Implementar ranking

```bash
git add src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "feat: implementa ranking de jogadores por pontuacao"
```

### Commit 2 — Adicionar testes do ranking

```bash
git add src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
git commit -m "test: adiciona testes de ranking no gerenciador de pontos"
```

## Checklist da Parte 2

- [ ] Ranking implementado.
- [ ] Ranking ordena por maior pontuação.
- [ ] Teste de ranking criado.
- [ ] Testes antigos continuam passando.
- [ ] Não houve alteração indevida na regra de pontuação existente.

---

# PARTE 3 — Integrar resposta, validação e pontuação no ServidorService

**Responsável:** Integrante 3  
**Foco:** transformar o fluxo do servidor em um jogo mais completo.

## Problema atual

O servidor envia a pergunta, mas ainda não lê a resposta do cliente, não valida se o jogador acertou ou errou e não aplica pontuação no fluxo real.

Com isso, `Pergunta`, `BancoDePerguntas` e `GerenciadorDePontos` existem, mas ainda estão pouco integrados ao funcionamento do servidor.

## Arquivos principais

```text
src/main/java/br/com/kahoot/ServidorService.java
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/main/java/br/com/kahoot/Pergunta.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

## Correção sugerida

Depois de enviar a pergunta e as alternativas, o servidor deve:

1. Solicitar resposta.
2. Ler resposta do cliente.
3. Validar se é número.
4. Comparar com a resposta correta.
5. Adicionar pontos se acertar.
6. Enviar resultado ao cliente.
7. Enviar pontuação final.
8. Enviar `FIM`.

## Protocolo sugerido

```text
BEM_VINDO
PERGUNTA: <enunciado>
ALT: 0 - <alternativa>
ALT: 1 - <alternativa>
ALT: 2 - <alternativa>
FIM_PERGUNTA
RESPONDA
RESULTADO: ACERTO
PONTOS: 10
FIM
```

Ou, em caso de erro:

```text
RESULTADO: ERRO
PONTOS: 0
FIM
```

## Ajuste necessário no cliente

A Parte 3 depende da Parte 1. O cliente precisará enviar uma resposta quando receber `RESPONDA`.

Exemplo de lógica futura no `Cliente.java`:

```java
if (linha.equals("RESPONDA")) {
    System.out.print("Digite sua resposta: ");
    String resposta = scanner.nextLine();
    out.println(resposta);
}
```

## Testes unitários recomendados

Adicionar ou ajustar testes em `ServidorServiceTest`:

```text
deveEnviarPerguntaEAlternativasParaCliente
deveLerRespostaDoCliente
deveEnviarResultadoDeAcertoQuandoRespostaCorreta
deveEnviarResultadoDeErroQuandoRespostaIncorreta
deveEnviarPontuacaoFinal
```

## Cuidados importantes

- Não misturar muita regra diretamente no `Servidor.java`.
- Manter o `Servidor.java` apenas iniciando conexão e chamando `ServidorService`.
- Concentrar a regra do jogo no `ServidorService`.
- Evitar dependência difícil de testar com `Socket`, se possível.
- Fazer commits pequenos, porque esta é a parte com maior risco de quebrar o projeto.

## Commits recomendados

### Commit 1 — Adicionar protocolo de resposta no servidor

```bash
git add src/main/java/br/com/kahoot/ServidorService.java
git commit -m "feat: adiciona leitura de resposta no servidor"
```

### Commit 2 — Integrar pontuação ao fluxo do servidor

```bash
git add src/main/java/br/com/kahoot/ServidorService.java src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "feat: integra pontuacao ao fluxo do servidor"
```

### Commit 3 — Ajustar cliente para enviar resposta

```bash
git add src/main/java/br/com/kahoot/Cliente.java
git commit -m "feat: permite cliente enviar resposta ao servidor"
```

### Commit 4 — Adicionar testes do fluxo de resposta

```bash
git add src/test/java/br/com/kahoot/ServidorServiceTest.java
git commit -m "test: adiciona testes para resposta e pontuacao no servidor"
```

## Checklist da Parte 3

- [ ] Servidor solicita resposta.
- [ ] Cliente envia resposta.
- [ ] Servidor valida resposta numérica.
- [ ] Servidor identifica acerto ou erro.
- [ ] Pontuação é aplicada em caso de acerto.
- [ ] Resultado é enviado ao cliente.
- [ ] Testes do servidor foram atualizados.

---

# PARTE 4 — Limpar target, validar testes e atualizar evidências

**Responsável:** Integrante 4  
**Foco:** garantir que o projeto não tenha evidências antigas e que os testes estejam coerentes com o código atual.

## Problema atual

A pasta `target` pode conter relatórios antigos de execuções anteriores. Isso pode confundir a defesa e a validação do Jenkins, principalmente se houver nomes antigos como `GerenciadorPontosTest`, enquanto o código atual usa `GerenciadorDePontosTest`.

## Arquivos e pastas envolvidos

```text
target/
pom.xml
Jenkinsfile
README.md
```

## Correção sugerida

Rodar:

```bash
mvn clean test
```

Esse comando remove a pasta `target` antiga e recria os relatórios de teste com base no código atual.

Depois, verificar:

```text
target/surefire-reports/
target/*.jar
```

## Importante sobre Git

Normalmente, a pasta `target/` não deve ser versionada. Verificar se existe `.gitignore` com:

```text
target/
```

Se não existir, criar ou atualizar o `.gitignore`.

## Ajuste recomendado no Jenkins

O `Jenkinsfile` deve continuar executando:

```bash
mvn clean test
mvn package
```

E publicando relatórios com:

```groovy
junit 'target/surefire-reports/*.xml'
```

E artefatos com:

```groovy
archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
```

## Atenção ao bloco de e-mail

Se o Jenkins não tiver SMTP configurado, o bloco de envio de e-mail pode falhar. Existem duas opções:

### Opção A — Remover temporariamente o envio de e-mail

Recomendado se o grupo não vai demonstrar notificação por e-mail.

### Opção B — Manter e documentar limitação

Recomendado se o professor pediu notificação, mas o SMTP ainda não foi configurado.

## Commits recomendados

### Commit 1 — Garantir target ignorado

```bash
git add .gitignore
git commit -m "chore: ignora arquivos gerados pelo maven"
```

### Commit 2 — Ajustar Jenkinsfile se necessário

```bash
git add Jenkinsfile
git commit -m "ci: ajusta pipeline para testes e artefatos do maven"
```

### Commit 3 — Atualizar README com validação dos testes

```bash
git add README.md
git commit -m "docs: atualiza instrucoes de testes e evidencias do jenkins"
```

## Checklist da Parte 4

- [ ] `mvn clean test` executado localmente.
- [ ] Relatórios antigos removidos.
- [ ] Relatórios atuais gerados.
- [ ] `.gitignore` ignora `target/`.
- [ ] Jenkins executa testes.
- [ ] Jenkins publica relatórios JUnit.
- [ ] Jenkins arquiva o `.jar`.
- [ ] README atualizado, se necessário.

---

# 4. Sequência geral de commits sugerida

Abaixo está uma sequência segura para aplicar tudo sem misturar responsabilidades.

## Integrante 1 — Cliente

```bash
git add src/main/java/br/com/kahoot/Cliente.java
git commit -m "fix: faz cliente ler todas as mensagens do servidor"
```

```bash
git add README.md
git commit -m "docs: documenta fluxo de execucao do cliente"
```

## Integrante 2 — Ranking

```bash
git add src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "feat: implementa ranking de jogadores por pontuacao"
```

```bash
git add src/test/java/br/com/kahoot/GerenciadorDePontosTest.java
git commit -m "test: adiciona testes de ranking no gerenciador de pontos"
```

## Integrante 3 — Servidor, resposta e pontuação

```bash
git add src/main/java/br/com/kahoot/ServidorService.java
git commit -m "feat: adiciona leitura de resposta no servidor"
```

```bash
git add src/main/java/br/com/kahoot/ServidorService.java src/main/java/br/com/kahoot/GerenciadorDePontos.java
git commit -m "feat: integra pontuacao ao fluxo do servidor"
```

```bash
git add src/main/java/br/com/kahoot/Cliente.java
git commit -m "feat: permite cliente enviar resposta ao servidor"
```

```bash
git add src/test/java/br/com/kahoot/ServidorServiceTest.java
git commit -m "test: adiciona testes para resposta e pontuacao no servidor"
```

## Integrante 4 — Testes, Jenkins e documentação

```bash
git add .gitignore
git commit -m "chore: ignora arquivos gerados pelo maven"
```

```bash
git add Jenkinsfile
git commit -m "ci: ajusta pipeline para testes e artefatos do maven"
```

```bash
git add README.md
git commit -m "docs: atualiza instrucoes de testes e evidencias do jenkins"
```

---

# 5. Comandos de validação antes do push

Antes de enviar para o GitHub:

```bash
mvn clean test
```

Depois:

```bash
git status
git log --oneline --decorate -n 10
git push origin Backend
```

Se o projeto estiver na branch `main`, ajustar conforme o fluxo do grupo.

---

# 6. Checklist final antes da entrega

- [ ] Cliente lê todas as mensagens do servidor.
- [ ] Cliente envia resposta quando solicitado.
- [ ] Servidor recebe resposta.
- [ ] Servidor valida resposta correta/incorreta.
- [ ] Servidor aplica pontuação.
- [ ] Ranking implementado.
- [ ] Testes de ranking criados.
- [ ] Testes do servidor atualizados.
- [ ] `mvn clean test` passando.
- [ ] `target/` não versionado.
- [ ] Jenkins executando pipeline com sucesso.
- [ ] Relatório JUnit publicado.
- [ ] `.jar` arquivado no Jenkins.
- [ ] README atualizado com execução, testes e Jenkins.
- [ ] Commits divididos por responsabilidade.

---

# 7. Conclusão

Este guia complementa o Guia 02 com as correções pendentes identificadas na revisão do código atual. A prioridade deve ser:

1. Corrigir o cliente para ler o protocolo completo.
2. Implementar ranking.
3. Integrar resposta e pontuação ao servidor.
4. Limpar evidências antigas e validar tudo com `mvn clean test`.

Seguindo essa ordem, o grupo consegue evoluir o MiniKahoot sem quebrar a estrutura atual e com commits claros para apresentar na defesa.
