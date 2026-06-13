# MiniKahoot — Guia 02: Melhorias de Código e Testes Unitários com Jenkins em Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**Branches utilizadas:** `main` e `Backend`  
**CI/CD adotado:** Jenkins em container Docker  
**Objetivo:** organizar as melhorias técnicas do código atual e dividir o trabalho em 4 partes estratégicas.

---

## 1. Objetivo deste guia

Este guia descreve melhorias no código do MiniKahoot considerando:

- Código Java/Maven existente.
- Uso de sockets TCP.
- Classes de perguntas.
- Gerenciamento de pontuação.
- Serviço do servidor.
- Cliente via terminal.
- Testes unitários com JUnit e Mockito.
- Jenkins rodando em container Docker.
- Pipeline validando build, testes, package, relatórios e artefatos.
- Necessidade de contribuições significativas de todos os membros.

As melhorias devem ser feitas depois que o Jenkins em Docker estiver funcionando na branch `Backend`.

---

## 2. Relação com o ambiente de CI/CD

Antes de iniciar as melhorias grandes, o grupo deve garantir que estes arquivos estejam criados:

```text
Dockerfile.jenkins
docker-compose.yml
Jenkinsfile
```

E que o Jenkins suba com:

```bash
docker compose up -d --build
```

A partir disso, cada melhoria deve passar por:

```text
mvn test local
↓
commit na Backend
↓
pipeline Jenkins
↓
relatório JUnit
↓
artefato .jar
```

Essa ordem evita que as melhorias quebrem o projeto sem o grupo perceber.

---

## 3. Ordem recomendada de execução

```text
1. Padronizar ambiente local com Java 17
2. Garantir mvn test funcionando localmente
3. Criar Dockerfile.jenkins
4. Criar docker-compose.yml
5. Criar Jenkinsfile
6. Subir Jenkins em container Docker
7. Validar pipeline no Jenkins
8. Melhorar perguntas e validações
9. Melhorar pontuação e ranking
10. Melhorar servidor e protocolo
11. Melhorar cliente e documentação
12. Revisar PRs
13. Fazer merge para main
```

---

## 4. Divisão estratégica em 4 partes

# PARTE 1 — Perguntas, alternativas e validações

**Responsável:** Integrante 1  
**Foco:** domínio das perguntas do quiz.

## Arquivos prováveis

```text
src/main/java/br/com/kahoot/Pergunta.java
src/main/java/br/com/kahoot/Perguntas.java
src/test/java/br/com/kahoot/PerguntaTest.java
src/test/java/br/com/kahoot/PerguntasTest.java
```

## Melhorias sugeridas

- Validar enunciado vazio ou nulo.
- Validar lista de alternativas vazia.
- Garantir quantidade mínima de alternativas.
- Validar resposta correta dentro do intervalo das alternativas.
- Impedir alteração externa da lista de alternativas.
- Criar método para formatar pergunta para envio ao cliente.
- Criar banco inicial de perguntas mais organizado.
- Adicionar testes unitários relevantes ao domínio.

## Testes unitários sugeridos

| Teste | Objetivo |
|---|---|
| `deveCriarPerguntaValida` | Garante criação correta de pergunta |
| `naoDeveCriarPerguntaComEnunciadoVazio` | Valida enunciado |
| `naoDeveCriarPerguntaSemAlternativas` | Valida alternativas |
| `naoDeveAceitarRespostaForaDoIntervalo` | Valida índice da resposta correta |
| `deveRetornarAlternativasImutaveis` | Evita alteração externa |
| `deveFormatarPerguntaParaEnvio` | Garante texto adequado para cliente |

## Evidência esperada

- Commit do integrante.
- Testes passando localmente com `mvn test`.
- Pipeline Jenkins passando.
- Relatório JUnit publicado.
- Stage relacionado no `Jenkinsfile`.

## Exemplo de commit

```bash
git add src/main/java/br/com/kahoot/Pergunta.java src/test/java/br/com/kahoot/PerguntaTest.java
git commit -m "test: adiciona validacoes e testes para perguntas"
```

---

# PARTE 2 — Pontuação, acertos, erros e ranking

**Responsável:** Integrante 2  
**Foco:** regras de pontuação do jogo.

## Arquivos prováveis

```text
src/main/java/br/com/kahoot/GerenciadorPontos.java
src/test/java/br/com/kahoot/GerenciadorPontosTest.java
```

## Melhorias sugeridas

- Padronizar pontuação por acerto.
- Impedir pontuação negativa.
- Criar penalidade opcional por erro.
- Criar bônus por resposta rápida.
- Criar método de ranking ordenado.
- Validar jogador inexistente.
- Separar regra de pontuação da comunicação via socket.

## Testes unitários sugeridos

| Teste | Objetivo |
|---|---|
| `deveAdicionarPontosQuandoJogadorAcerta` | Testa acerto |
| `naoDeveAdicionarPontosQuandoJogadorErra` | Testa erro |
| `naoDevePermitirPontuacaoNegativa` | Evita pontuação inválida |
| `deveOrdenarRankingPorMaiorPontuacao` | Testa ranking |
| `deveEmpatarJogadoresComMesmaPontuacao` | Testa empate |
| `deveRetornarZeroParaJogadorNovo` | Estado inicial |
| `deveAplicarBonusPorRespostaRapida` | Regra de bônus |

## Evidência esperada

- Testes de domínio.
- Relatório JUnit no Jenkins.
- Histórico de commits.
- PR revisado.
- Pipeline executado no container Jenkins.

## Exemplo de commit

```bash
git add src/main/java/br/com/kahoot/GerenciadorPontos.java src/test/java/br/com/kahoot/GerenciadorPontosTest.java
git commit -m "feat: implementa ranking e regras de pontuacao"
```

---

# PARTE 3 — Servidor, protocolo e múltiplos clientes

**Responsável:** Integrante 3  
**Foco:** comunicação do servidor.

## Arquivos prováveis

```text
src/main/java/br/com/kahoot/Servidor.java
src/main/java/br/com/kahoot/ServidorService.java
src/test/java/br/com/kahoot/ServidorServiceTest.java
```

## Melhorias sugeridas

- Definir protocolo simples de mensagens.
- Criar mensagens padronizadas:
  - `BEM_VINDO`
  - `PERGUNTA`
  - `RESPOSTA`
  - `RESULTADO`
  - `RANKING`
  - `FIM`
- Separar lógica de rede da lógica de jogo.
- Evitar que `ServidorService` dependa diretamente de muitos detalhes de `Socket`.
- Melhorar tratamento de exceções.
- Preparar atendimento de múltiplos clientes.
- Evitar mocks difíceis de `Socket` quando possível, usando abstrações.

## Sugestão de refactoring

Criar uma interface para facilitar testes:

```java
public interface ClienteConexao {
    void enviarMensagem(String mensagem);
    String lerMensagem();
}
```

Depois, criar uma implementação real:

```java
public class SocketClienteConexao implements ClienteConexao {
    private final Socket socket;

    public SocketClienteConexao(Socket socket) {
        this.socket = socket;
    }

    // implementação real usando InputStream e OutputStream
}
```

Com isso, os testes podem mockar `ClienteConexao`, não `Socket`.

## Por que esse refactoring ajuda?

Esse refactoring reduz acoplamento com `java.net.Socket`. Isso facilita:

- Testes unitários.
- Simulação de mensagens.
- Tratamento de erro.
- Evolução futura para múltiplos clientes.
- Execução consistente no Jenkins.

## Testes unitários sugeridos

| Teste | Objetivo |
|---|---|
| `deveEnviarMensagemDeBoasVindas` | Valida início da conexão |
| `naoDeveEnviarMensagemVazia` | Valida mensagem inválida |
| `deveEnviarPerguntaFormatada` | Testa envio de pergunta |
| `deveReceberRespostaDoCliente` | Testa leitura |
| `deveTratarErroDeConexao` | Testa exceção |
| `deveAtenderMaisDeUmCliente` | Testa múltiplos clientes |

## Evidência esperada

- Refactoring em commit separado.
- Testes antes e depois da alteração.
- Jenkins mostrando testes passando.
- PR explicando por que a interface foi criada.
- Artefato `.jar` gerado após os testes.

## Exemplo de commit

```bash
git add src/main/java/br/com/kahoot/ServidorService.java src/test/java/br/com/kahoot/ServidorServiceTest.java
git commit -m "refactor: separa comunicacao de socket da regra do servidor"
```

---

# PARTE 4 — Cliente, terminal, execução e documentação

**Responsável:** Integrante 4  
**Foco:** experiência do jogador, execução e documentação.

## Arquivos prováveis

```text
src/main/java/br/com/kahoot/Cliente.java
README.md
```

## Melhorias sugeridas

- Solicitar nome do jogador.
- Exibir perguntas de forma clara.
- Ler resposta do jogador pelo terminal.
- Validar entrada inválida.
- Medir tempo de resposta.
- Exibir resultado final.
- Exibir ranking ao final.
- Documentar como executar servidor e cliente.
- Documentar como rodar testes.
- Documentar como subir Jenkins em Docker.
- Documentar como interpretar o pipeline Jenkins.

## Testes sugeridos

Como cliente de terminal é mais difícil de testar diretamente, recomenda-se separar a lógica:

```text
EntradaUsuario
FormatadorMensagem
ValidadorResposta
```

Testes possíveis:

| Teste | Objetivo |
|---|---|
| `deveAceitarRespostaNumericaValida` | Entrada correta |
| `naoDeveAceitarTextoComoResposta` | Entrada inválida |
| `naoDeveAceitarAlternativaForaDoIntervalo` | Validação de resposta |
| `deveFormatarRankingParaExibicao` | Saída final |
| `deveExibirMensagemDeFimDeJogo` | Encerramento |

## Evidência esperada

- README atualizado.
- Testes de validação de resposta.
- Jenkins em Docker passando.
- Artefato `.jar` arquivado.

## Exemplo de commit

```bash
git add src/main/java/br/com/kahoot/Cliente.java README.md
git commit -m "feat: melhora interacao do cliente via terminal"
```

---

# 5. Testes unitários mínimos esperados

Para a NP2, os testes precisam ser relevantes ao domínio do projeto. Portanto, evitar testes genéricos ou artificiais.

## Testes recomendados por área

| Área | Quantidade mínima sugerida |
|---|---:|
| Perguntas | 5 testes |
| Pontuação | 6 testes |
| Servidor/Protocolo | 5 testes |
| Cliente/Validação | 4 testes |

Total sugerido: **20 testes relevantes**.

---

# 6. Refactorings recomendados

A NP2 cobra quais refactorings foram aplicados, por quê e com evidência em commits/PRs.

| Refactoring | Motivo | Evidência esperada |
|---|---|---|
| Extrair interface `ClienteConexao` | Facilitar testes e reduzir dependência de `Socket` | Commit `refactor` + testes |
| Separar regra de pontuação | Evitar regra misturada com comunicação | Testes de `GerenciadorPontos` |
| Criar formatadores de mensagem | Padronizar protocolo | Testes de formatação |
| Validar entidades de domínio | Evitar estados inválidos | Testes de `Pergunta` |
| Separar entrada do terminal | Facilitar testes do cliente | Testes de validação |
| Containerizar Jenkins | Reproduzir ambiente de CI/CD | `Dockerfile.jenkins` + `docker-compose.yml` |

---

# 7. Integração das melhorias com Jenkins em Docker

A cada melhoria:

1. Rodar localmente:

```bash
mvn test
```

2. Fazer commit na branch `Backend`.
3. Enviar para o GitHub.
4. Rodar ou aguardar Jenkins executar o job.
5. Verificar:
   - Build.
   - Testes.
   - Relatórios JUnit.
   - Package.
   - Artefato `.jar`.

Se o Jenkins falhar, corrigir antes do merge para `main`.

---

# 8. Padrão de commits

Usar commits pequenos e claros:

```text
feat: nova funcionalidade
fix: correção de bug
test: criação ou ajuste de testes
refactor: melhoria interna sem mudar comportamento
docs: documentação
ci: pipeline e automação
```

Exemplos:

```bash
git commit -m "test: adiciona testes de validacao de perguntas"
git commit -m "feat: implementa ranking de jogadores"
git commit -m "refactor: extrai interface de conexao do cliente"
git commit -m "ci: adiciona Jenkins em container Docker"
git commit -m "docs: adiciona instrucoes de execucao do servidor"
```

---

# 9. Checklist final das melhorias

- [ ] `mvn test` funcionando localmente com Java 17.
- [ ] `Dockerfile.jenkins` criado.
- [ ] `docker-compose.yml` criado.
- [ ] `Jenkinsfile` criado e validado.
- [ ] Jenkins subindo em container Docker.
- [ ] Testes de perguntas criados.
- [ ] Testes de pontuação ampliados.
- [ ] Ranking implementado.
- [ ] Protocolo de mensagens documentado.
- [ ] Servidor com melhor tratamento de erros.
- [ ] Cliente com melhor interação.
- [ ] Refactorings documentados.
- [ ] Commits significativos de todos os membros.
- [ ] PRs revisados antes do merge.
- [ ] README atualizado.
- [ ] Jenkins validando as melhorias.