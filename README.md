# Mini Kahoot - Aplicação Cliente/Servidor em Java

## Descrição

Este projeto implementa uma aplicação de quiz cliente/servidor em Java usando sockets TCP e JavaFX.

A nova arquitetura oferece:

* Servidor com perguntas de quiz, recebimento do nome do jogador e contagem de pontuação.
* Cliente gráfico em JavaFX para entrada de nome, seleção de alternativas e exibição de ranking.
* Persistência de ranking local em arquivo `ranking.txt`.
* Envio do top 5 melhores jogadores ao final do jogo.

---

## Arquitetura e Componentes

### `Servidor`

* Classe: `br.com.kahoot.Servidor`
* Função: inicializa servidor TCP na porta `12345`, aceita um cliente, recebe o nome do jogador, envia perguntas, recebe respostas e atualiza pontuação.
* Finaliza o jogo enviando um resumo da pontuação e o ranking top 5.

### `ClienteGUI`

* Classe: `br.com.kahoot.ClienteGUI`
* Função: UI JavaFX para o jogador informar nome, conectar ao servidor, responder perguntas e visualizar a pontuação e o ranking.
* Exibe campos de nome, IP, porta, status, pergunta, alternativas, pontuação e ranking.

### Modelo de dados

* `br.com.kahoot.Pergunta` - representa uma pergunta, alternativas e resposta correta.
* `br.com.kahoot.Perguntas` - lista de perguntas do quiz.
* `br.com.kahoot.GerenciadorPontos` - calcula a pontuação do jogador durante o jogo.
* `br.com.kahoot.Ranking` - persiste e ordena as pontuações, retornando o top 5.

---

## Funcionamento Atualizado

### Fluxo geral

1. O cliente abre a interface JavaFX.
2. O jogador informa seu nome, IP e porta.
3. O cliente se conecta ao servidor e envia o nome.
4. O servidor responde com boas-vindas e envia perguntas em sequência.
5. O cliente mostra cada pergunta e envia a resposta selecionada.
6. Ao final, o servidor calcula a pontuação, salva no ranking e retorna o top 5.
7. O cliente exibe a pontuação final e o ranking top 5.

### Ranking

* O ranking é armazenado no arquivo `ranking.txt`.
* Cada entrada tem o formato `Nome|Pontuacao`.
* O servidor carrega o ranking no início do jogo, adiciona a pontuação atual e salva novamente.
* O ranking enviado ao cliente inclui os 5 melhores jogadores.

---

## Estrutura do Projeto

```text
src/
└── main/
    └── java/
        └── br/
            └── com/
                └── kahoot/
                    ├── ClienteGUI.java
                    ├── Cliente.java
                    ├── GerenciadorPontos.java
                    ├── Pergunta.java
                    ├── Perguntas.java
                    ├── Ranking.java
                    └── Servidor.java
```

---

## Como Executar

### Pré-requisito

* Java 17
* Maven instalado
* Conexão local via `localhost`

### 1. Abrir terminal na pasta do projeto

```powershell
cd "c:\Users\samur\OneDrive\Documents\Inatel\periodo 08\C14\minikahoot\minikahoot"
```

### 2. Compilar o projeto

```powershell
mvn compile
```

### 3. Executar o servidor

```powershell
mvn exec:java
```

### 4. Executar o cliente em outro terminal

```powershell
mvn javafx:run
```

### 5. Usar o cliente

* Informe o `Nome` antes de se conectar.
* Use `localhost` e porta `12345`.
* Clique em `Conectar`.
* Selecione a alternativa correta e clique em `Responder`.
* No final do jogo, o cliente mostrará a pontuação e o `Ranking Top 5`.

---

## Testes

Se desejar executar os testes unitários existentes:

```powershell
mvn test
```

---

## Observações

* O servidor deve estar rodando antes de conectar o cliente.
* O ranking é persistido em `ranking.txt`.
* O projeto pode ser estendido para suportar vários jogadores simultâneos e interface de servidor mais avançada.

---

## Autores

Luís Henrique de Souza Côrtes Moreira - GES 642
Rafael Mello Barbosa da Silva - GES 609
Gabriel Bissacot Fraguas - GEC 1363
Samuel Almeida Ralise - GEC 1993
