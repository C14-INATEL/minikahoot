# MiniKahoot — Guia 02 Atualizado: Melhorias de Código e Testes Unitários com Jenkins em Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**Branches utilizadas:** `main` e `Backend`  
**CI/CD adotado:** Jenkins em container Docker  
**Base da revisão:** código atual enviado em `minikahoot(2).zip`  
**Objetivo:** atualizar o guia de melhorias conforme a estrutura real do código atual, removendo referências que não existem mais, como `ClienteGUI`, `Ranking.java` e `ranking.txt`.

---

## 1. Situação atual do projeto revisado

O projeto atual está em Java/Maven e possui uma aplicação cliente/servidor simples usando sockets TCP. O servidor aceita um cliente, envia uma mensagem de boas-vindas e encerra a conexão. Também existem classes de domínio para perguntas e pontuação, além de testes unitários com JUnit 5 e Mockito.

### Estrutura principal encontrada

```text
pom.xml
Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
README.md
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/main/java/br/com/kahoot/Cliente.java
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/main/java/br/com/kahoot/Pergunta.java
src/main/java/br/com/kahoot/Servidor.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
src/test/java/br/com/kahoot/GerenciadorPontosTest.java
src/test/java/br/com/kahoot/PerguntaTest.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

### Tecnologias atuais

| Item | Situação atual |
|---|---|
| Linguagem | Java 17 |
| Build | Maven |
| Interface | Terminal |
| Comunicação | Socket TCP |
| Testes | JUnit 5 e Mockito |
| CI/CD | Jenkins em container Docker |
| Pipeline | `Jenkinsfile` com ambiente, build, testes, package, relatórios e artefatos |
| Cliente gráfico | Não existe nesta versão atual |
| Ranking persistido | Não existe nesta versão atual |

---

## 2. Resultado da revisão do código atual

## 2.1 O código atual compila estaticamente com `javac`

Foi feita uma verificação estática das classes de produção com:

```bash
javac -d /tmp/mk_classes src/main/java/br/com/kahoot/*.java
```

As classes principais compilaram com sucesso usando `javac`. Porém, não foi possível executar `mvn test` no ambiente de revisão porque o comando `mvn` não estava instalado.

### Ação recomendada no computador do grupo

Rodar obrigatoriamente na pasta onde está o `pom.xml`:

```bash
mvn clean test
mvn clean package
```

---

## 2.2 README está desatualizado em relação ao projeto Maven

O `README.md` atual ainda descreve uma estrutura antiga:

```text
src/br/com/kahoot/Servidor.java
src/br/com/kahoot/Cliente.java
```

Mas o projeto atual usa a estrutura Maven:

```text
src/main/java/br/com/kahoot
src/test/java/br/com/kahoot
```

Também faltam no README informações sobre:

- Maven.
- Java 17.
- Testes unitários.
- Jenkins.
- Dockerfile do Jenkins.
- `docker-compose.yml`.
- `Jenkinsfile`.
- Classes `Pergunta`, `BancoDePerguntas`, `GerenciadorDePontos` e `ServidorService`.

### Commit recomendado

```bash
git add README.md
git commit -m "docs: atualiza README conforme estrutura Maven do projeto"
```

---

## 2.3 Servidor ainda está muito simples

A classe `Servidor.java` atualmente faz apenas:

1. Abre um `ServerSocket` na porta `12345`.
2. Aguarda um cliente.
3. Envia `Bem-vindo ao MiniKahoot!`.
4. Fecha a conexão.

Isso está coerente com o cliente atual, mas ainda não usa:

```text
BancoDePerguntas.java
Pergunta.java
GerenciadorDePontos.java
```

Portanto, para a entrega, o grupo deve deixar claro se a versão final é apenas uma demonstração de socket TCP ou se pretende integrar o fluxo completo de quiz.

### Melhoria recomendada

Integrar o servidor com o banco de perguntas:

```java
BancoDePerguntas banco = new BancoDePerguntas();
Pergunta pergunta = banco.obterPergunta(0);
out.println(pergunta.formatarParaEnvio());
```

Depois, o cliente precisaria ler mais de uma linha ou o protocolo precisaria usar mensagens delimitadas.

---

## 2.4 `ServidorService` está separado, mas ainda é pouco usado

A classe `ServidorService.java` possui o método:

```java
public void enviarBoasVindas(Socket socket) throws Exception
```

Esse método é testado em `ServidorServiceTest`, mas o `Servidor.java` ainda escreve diretamente no socket usando `PrintWriter`. Para melhorar a arquitetura, o servidor deveria usar o service:

```java
ServidorService service = new ServidorService();
service.enviarBoasVindas(cliente);
```

### Commit recomendado

```bash
git add src/main/java/br/com/kahoot/Servidor.java src/main/java/br/com/kahoot/ServidorService.java
git commit -m "refactor: usa ServidorService no fluxo principal do servidor"
```

---

## 2.5 Regra de pontuação permite pontuação negativa

A classe `GerenciadorDePontos` calcula os pontos assim:

```java
float pontosGanhos = 100 * (15 - tempoPercorrido);
```

Com isso, se `tempoPercorrido` for maior que `15`, a pontuação fica negativa. O teste atual `devePermitirPontuacaoNegativaQuandoTempoExcedeLimite` confirma esse comportamento.

O grupo precisa escolher uma abordagem e explicar na defesa:

| Opção | Decisão |
|---|---|
| Manter pontuação negativa | Tratar como penalidade por demora |
| Impedir pontuação negativa | Usar `Math.max(0, pontosGanhos)` |

### Recomendação

Para um quiz acadêmico, é mais simples explicar que o jogador nunca perde pontos por responder devagar. Nesse caso, alterar para:

```java
float pontosGanhos = Math.max(0, 100 * (15 - tempoPercorrido));
```

E trocar o teste atual por:

```java
void naoDevePermitirPontuacaoNegativaQuandoTempoExcedeLimite()
```

---

## 2.6 Falta ranking real no código atual

O guia anterior mencionava ranking, mas a versão atual do código não possui `Ranking.java` nem `ranking.txt`. O que existe é apenas pontuação por índice de jogador dentro de `GerenciadorDePontos`.

Para a entrega, existem duas opções:

| Opção | Como documentar |
|---|---|
| Não implementar ranking | Dizer que há pontuação individual, mas ranking ficou como melhoria futura |
| Implementar ranking simples | Criar método que ordena jogadores pela pontuação |

### Melhoria sugerida

Adicionar em `GerenciadorDePontos` um método para ranking:

```java
public String gerarRanking() {
    StringBuilder ranking = new StringBuilder();
    for (int i = 0; i < jogadores.length; i++) {
        ranking.append(jogadores[i])
               .append(" - ")
               .append(pontos[i])
               .append(" pontos\n");
    }
    return ranking.toString();
}
```

Depois, criar testes para validar a saída.

---

# 3. Ordem recomendada de execução atualizada

```text
1. Garantir que o projeto ativo é a pasta que contém o pom.xml
2. Rodar mvn clean test localmente
3. Atualizar README.md para Maven, Java 17, Jenkins e Docker
4. Usar ServidorService dentro de Servidor.java
5. Decidir regra final de pontuação negativa
6. Corrigir ou documentar a pontuação negativa
7. Definir se ranking será entregue ou ficará como melhoria futura
8. Integrar BancoDePerguntas ao fluxo do servidor, se houver tempo
9. Subir Jenkins com docker compose up -d --build
10. Executar pipeline Jenkins na branch Backend
11. Conferir relatórios JUnit
12. Conferir artefato .jar
13. Revisar PRs
14. Fazer merge da Backend para main
```

---

# 4. Divisão estratégica atualizada em 4 partes

# PARTE 1 — Perguntas, alternativas e banco de perguntas

**Responsável:** Integrante 1  
**Foco:** domínio das perguntas do quiz.

## Arquivos atuais

```text
src/main/java/br/com/kahoot/Pergunta.java
src/main/java/br/com/kahoot/BancoDePerguntas.java
src/test/java/br/com/kahoot/PerguntaTest.java
src/test/java/br/com/kahoot/BancoDePerguntasTest.java
```

## O que já existe

- Validação de enunciado nulo ou vazio.
- Validação de alternativas nulas ou insuficientes.
- Validação de resposta correta fora do intervalo.
- Cópia defensiva do array de alternativas.
- Formatação da pergunta para envio.
- Banco inicial com 3 perguntas.
- Testes unitários para pergunta e banco de perguntas.

## Melhorias sugeridas

- Validar alternativas vazias individualmente, por exemplo `""` ou `"   "`.
- Evitar alternativas duplicadas.
- Aumentar o número de perguntas iniciais.
- Padronizar se a resposta correta será tratada internamente como índice 0-based ou exibida como 1-based.
- Documentar no README que `Pergunta` usa índice interno começando em `0`.

## Testes adicionais sugeridos

| Teste | Objetivo |
|---|---|
| `naoDeveCriarPerguntaComAlternativaVazia` | Evita alternativa sem conteúdo |
| `naoDeveCriarPerguntaComAlternativasDuplicadas` | Evita opções repetidas |
| `deveCarregarTresPerguntasIniciais` | Garante banco inicial esperado |
| `deveObterPerguntaPorIndiceValido` | Garante recuperação correta |

## Commit recomendado

```bash
git add src/main/java/br/com/kahoot/Pergunta.java src/main/java/br/com/kahoot/BancoDePerguntas.java src/test/java/br/com/kahoot/PerguntaTest.java src/test/java/br/com/kahoot/BancoDePerguntasTest.java
git commit -m "test: amplia validacoes de perguntas e banco de perguntas"
```

---

# PARTE 2 — Pontuação e regra de tempo

**Responsável:** Integrante 2  
**Foco:** pontuação dos jogadores.

## Arquivos atuais

```text
src/main/java/br/com/kahoot/GerenciadorDePontos.java
src/test/java/br/com/kahoot/GerenciadorPontosTest.java
```

## O que já existe

- Vetor de jogadores.
- Vetor de pontos.
- Pontuação calculada com base no tempo.
- Testes para pontos iniciais, acúmulo, resposta rápida, ID inválido e múltiplos jogadores.

## Ponto de atenção

O nome da classe é:

```text
GerenciadorDePontos
```

Mas o nome do arquivo de teste é:

```text
GerenciadorPontosTest
```

Isso compila porque a classe de teste não precisa ter o mesmo nome da classe testada. Porém, para padronização, recomenda-se renomear o teste para:

```text
GerenciadorDePontosTest.java
```

## Melhorias sugeridas

- Validar `getPontos(idCliente)` com ID inválido.
- Validar `numJogadores` menor ou igual a zero.
- Validar array de jogadores nulo.
- Impedir pontuação negativa ou documentar penalidade.
- Criar ranking ordenado por pontuação, se esse requisito for mantido.
- Retornar cópia defensiva do array de jogadores em `getJogadores()`.

## Testes adicionais sugeridos

| Teste | Objetivo |
|---|---|
| `naoDevePermitirPontuacaoNegativaQuandoTempoExcedeLimite` | Caso o grupo decida impedir negativos |
| `deveLancarErroAoBuscarPontosComIdInvalido` | Protege `getPontos` |
| `naoDeveCriarGerenciadorComJogadoresNulos` | Validação de entrada |
| `deveRetornarCopiaDefensivaDosJogadores` | Evita alteração externa |
| `deveGerarRankingOrdenadoPorPontuacao` | Se ranking for implementado |

## Commit recomendado

```bash
git add src/main/java/br/com/kahoot/GerenciadorDePontos.java src/test/java/br/com/kahoot/GerenciadorPontosTest.java
git commit -m "feat: ajusta regra de pontuacao e amplia testes"
```

---

# PARTE 3 — Servidor, service e protocolo TCP

**Responsável:** Integrante 3  
**Foco:** comunicação cliente/servidor.

## Arquivos atuais

```text
src/main/java/br/com/kahoot/Servidor.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

## O que já existe

- Servidor TCP na porta `12345`.
- Aceite de uma conexão por execução.
- Envio de mensagem de boas-vindas.
- `ServidorService` com método para enviar boas-vindas.
- Testes com Mockito simulando `Socket`.

## Melhorias sugeridas

- Fazer `Servidor.java` usar `ServidorService`.
- Evitar duplicação da mensagem `Bem-vindo ao MiniKahoot!`.
- Extrair a mensagem de boas-vindas para uma constante.
- Criar protocolo simples para perguntas e respostas.
- Integrar `BancoDePerguntas` ao servidor.
- Preparar loop para múltiplos clientes, se isso for exigido.
- Tratar fechamento de recursos com `try-with-resources`.

## Protocolo simples sugerido

```text
BEM_VINDO|Bem-vindo ao MiniKahoot!
PERGUNTA|Qual estrutura armazena pares chave-valor em Java?
ALT|1|List
ALT|2|Set
ALT|3|Map
ALT|4|Queue
FIM_PERGUNTA
RESULTADO|CORRETO|1000
FIM
```

## Testes adicionais sugeridos

| Teste | Objetivo |
|---|---|
| `deveUsarServidorServiceParaEnviarBoasVindas` | Reduz duplicação |
| `deveEnviarPerguntaFormatada` | Valida integração com `Pergunta` |
| `deveTratarErroDeConexaoSemDerrubarServidor` | Melhora robustez |
| `deveFecharSocketAposAtendimento` | Garante fechamento correto |

## Commit recomendado

```bash
git add src/main/java/br/com/kahoot/Servidor.java src/main/java/br/com/kahoot/ServidorService.java src/test/java/br/com/kahoot/ServidorServiceTest.java
git commit -m "refactor: centraliza comunicacao do servidor no service"
```

---

# PARTE 4 — Cliente, execução, README e CI/CD

**Responsável:** Integrante 4  
**Foco:** execução do projeto, documentação e Jenkins.

## Arquivos atuais

```text
src/main/java/br/com/kahoot/Cliente.java
README.md
pom.xml
Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
```

## O que já existe

- Cliente via terminal conectando em `localhost:12345`.
- Leitura de uma linha enviada pelo servidor.
- Maven configurado para Java 17.
- Jenkinsfile com stages de ambiente, build, testes, package, relatórios e artefatos.
- Dockerfile do Jenkins com Maven, Git e Docker CLI.
- Docker Compose expondo portas `8080` e `50000`.

## Melhorias sugeridas

- Atualizar README com comandos Maven:

```bash
mvn clean compile
mvn test
mvn clean package
mvn exec:java
```

- Explicar como executar o cliente:

```bash
java -cp target/classes br.com.kahoot.Cliente
```

- Explicar Jenkins:

```bash
docker compose up -d --build
```

- Documentar que o e-mail do Jenkins depende de SMTP configurado.
- Remover do README a estrutura antiga que usa `javac br/com/kahoot/*.java`.
- Documentar a seção `Uso de IA`, exigida pela NP2.

## Commit recomendado

```bash
git add README.md Jenkinsfile Dockerfile.jenkins docker-compose.yml pom.xml
git commit -m "docs: documenta execucao Maven e Jenkins em Docker"
```

---

# 5. Testes unitários encontrados e próximos passos

Foram encontrados **25 testes** anotados com `@Test`, distribuídos em:

| Classe de teste | Foco |
|---|---|
| `PerguntaTest` | Validação e formatação de perguntas |
| `BancoDePerguntasTest` | Banco inicial, adição, busca e imutabilidade |
| `GerenciadorPontosTest` | Pontuação por tempo e múltiplos jogadores |
| `ServidorServiceTest` | Envio de boas-vindas e mocks de socket |

## Próximos testes mais importantes

| Área | Teste recomendado |
|---|---|
| Pontuação | ID inválido em `getPontos` |
| Pontuação | Criação com jogadores nulos |
| Pergunta | Alternativa vazia |
| Pergunta | Alternativa duplicada |
| Servidor | Uso real de `ServidorService` pelo `Servidor` |
| Cliente | Tratamento quando servidor está indisponível |

---

# 6. Refactorings recomendados

| Refactoring | Motivo | Evidência esperada |
|---|---|---|
| Usar `ServidorService` dentro de `Servidor` | Evitar duplicação e melhorar testabilidade | Commit `refactor` + testes |
| Extrair constante da mensagem de boas-vindas | Evitar texto duplicado | Teste de mensagem mantido |
| Retornar cópia defensiva em `getJogadores()` | Evitar alteração externa do estado | Teste de imutabilidade |
| Validar entradas de `GerenciadorDePontos` | Evitar estados inválidos | Testes de exceção |
| Decidir regra de pontuação negativa | Evitar comportamento ambíguo | Teste atualizado ou documentação |
| Atualizar README para Maven | Evitar instruções erradas | Commit `docs` |

---

# 7. Integração com Jenkins em Docker

O projeto atual já possui os arquivos necessários:

```text
Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
```

## Comando para subir o Jenkins

```bash
docker compose up -d --build
```

## Etapas atuais do pipeline

| Stage | Função |
|---|---|
| `Ambiente - Java Maven Git` | Verifica Java, Maven e Git |
| `Build Maven` | Executa `mvn clean compile` |
| `Testes Unitarios` | Executa `mvn test` e publica JUnit |
| `Package JAR` | Executa `mvn clean package` |
| `Relatorios e Artefatos` | Arquiva `target/*.jar` e publica JUnit |
| `Validacao Final` | Mostra resumo do pipeline |

## Atenção ao e-mail

O `Jenkinsfile` usa:

```groovy
mail to: "${EMAIL_DESTINO}"
```

Isso só funciona se o Jenkins tiver SMTP configurado. Caso contrário, o build pode falhar na etapa de notificação. Para evitar problema na defesa, o grupo pode:

1. Configurar SMTP no Jenkins; ou
2. Remover temporariamente o envio de e-mail; ou
3. Documentar que a notificação por e-mail depende de configuração externa.

---

# 8. Checklist final atualizado

- [ ] Confirmar que o projeto ativo é a pasta com `pom.xml`.
- [ ] Rodar `mvn clean test` localmente.
- [ ] Rodar `mvn clean package` localmente.
- [ ] Atualizar README para estrutura Maven.
- [ ] Documentar Java 17.
- [ ] Documentar execução do servidor e do cliente.
- [ ] Documentar Jenkins em Docker.
- [ ] Documentar uso de IA.
- [ ] Usar `ServidorService` dentro de `Servidor`.
- [ ] Decidir pontuação negativa.
- [ ] Corrigir ou justificar pontuação negativa.
- [ ] Decidir se ranking será implementado ou listado como melhoria futura.
- [ ] Validar pipeline Jenkins.
- [ ] Conferir relatórios JUnit no Jenkins.
- [ ] Conferir artefato `.jar` arquivado.
- [ ] Revisar PRs.
- [ ] Fazer merge da `Backend` para `main`.
