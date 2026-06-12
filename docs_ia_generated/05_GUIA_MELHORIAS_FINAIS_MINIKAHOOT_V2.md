# MiniKahoot — Guia 05: Melhorias Finais de Coerência, E-mail no Jenkins e Estrutura

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**Branches utilizadas:** `main` e `Backend`  
**CI/CD adotado:** Jenkins em container Docker  
**Objetivo:** aplicar os ajustes finais identificados após a revisão completa do código atual, garantindo coerência entre implementação, documentação, pipeline com notificação por e-mail e estrutura de entrega.

---

## 1. Objetivo deste guia

Este guia reúne as melhorias finais recomendadas após a revisão do projeto `MiniKahoot v3`.

As melhorias principais do Guia 04 já foram aplicadas:

- Cliente lendo várias mensagens até `FIM`.
- Servidor enviando pergunta e alternativas.
- Cliente enviando resposta.
- Servidor validando acerto ou erro.
- Pontuação integrada ao fluxo.
- Ranking básico implementado no `GerenciadorDePontos`.
- Testes organizados para perguntas, banco de perguntas, pontuação e servidor.

Mesmo assim, ainda existem ajustes importantes para deixar o projeto mais coerente para entrega e defesa:

1. Atualizar o `README.md` para refletir o fluxo real atual.
2. Configurar SMTP no Jenkins para manter a notificação por e-mail funcionando.
3. Limpar a estrutura do repositório e remover arquivos gerados ou duplicados.

---

## 2. Estado atual do projeto

A estrutura principal esperada do projeto é:

```text
src/main/java/br/com/kahoot/
├── BancoDePerguntas.java
├── Cliente.java
├── GerenciadorDePontos.java
├── Pergunta.java
├── Servidor.java
└── ServidorService.java
```

E os testes principais são:

```text
src/test/java/br/com/kahoot/
├── BancoDePerguntasTest.java
├── GerenciadorDePontosTest.java
├── PerguntaTest.java
└── ServidorServiceTest.java
```

Fluxo funcional atual:

```text
Servidor
↓
ServidorService
↓
BancoDePerguntas
↓
Pergunta
↓
Cliente recebe pergunta e alternativas
↓
Cliente envia resposta
↓
ServidorService valida resposta
↓
GerenciadorDePontos calcula pontuação
↓
Servidor envia resultado, pontos e FIM
```

Exemplo de protocolo observado:

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

---

## 3. Divisão em 3 partes

# PARTE 1 — Atualização do README e coerência da documentação

**Responsável:** Integrante 1  
**Foco:** documentação do funcionamento real do projeto.

## Problema identificado

O `README.md` ainda pode estar descrevendo uma versão antiga do sistema, em que:

- O cliente apenas recebia uma mensagem simples.
- O servidor apenas enviava boas-vindas.
- O `ServidorService` ainda não processava pergunta, resposta, resultado e pontuação.

Isso ficou desatualizado em relação ao código atual.

## Objetivo da melhoria

Atualizar o `README.md` para explicar corretamente:

- Como o servidor funciona.
- Como o cliente funciona.
- Como é o protocolo de mensagens.
- Como a pontuação é calculada.
- Como executar servidor e cliente.
- Como rodar os testes.
- Como executar o Jenkins em Docker.
- Como a notificação por e-mail no Jenkins é configurada.
- O que está implementado e o que ficou como melhoria futura.

---

## Arquivos envolvidos

```text
README.md
```

---

## Correção sugerida

Atualizar a descrição das classes principais:

```md
## Estrutura principal

- `Servidor`: inicia o servidor TCP na porta `12345`, aguarda a conexão de um cliente e delega o atendimento para `ServidorService`.
- `Cliente`: conecta ao servidor, lê todas as mensagens do protocolo, exibe pergunta e alternativas, envia a resposta digitada pelo usuário e mostra resultado/pontuação.
- `ServidorService`: concentra o fluxo do jogo: boas-vindas, envio da pergunta, leitura da resposta, validação, pontuação e encerramento.
- `Pergunta`: representa uma pergunta do quiz, com enunciado, alternativas e índice da resposta correta.
- `BancoDePerguntas`: mantém a lista inicial de perguntas e permite consultar/adicionar perguntas.
- `GerenciadorDePontos`: calcula pontuação, impede pontuação negativa e permite obter ranking dos jogadores.
```

Adicionar também uma seção de protocolo:

````md
## Protocolo de comunicação

O servidor e o cliente usam mensagens em texto simples via socket TCP.

Exemplo de fluxo:

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
````

Adicionar uma observação honesta sobre melhoria futura:

```md
## Melhorias futuras

- Medir o tempo real de resposta do jogador no fluxo cliente-servidor.
- Exibir ranking completo ao final da partida.
- Permitir múltiplos jogadores simultâneos.
- Expandir o banco de perguntas.
```

Adicionar uma seção curta sobre e-mail no Jenkins:

```md
## Notificação por e-mail no Jenkins

O pipeline possui notificação por e-mail no bloco `post` do `Jenkinsfile`.
Para funcionar, o Jenkins precisa estar com SMTP configurado em:

Manage Jenkins → System → E-mail Notification

No caso de Gmail, é necessário usar senha de app, não a senha normal da conta.
```

---

## Testes esperados

Essa parte é documentação, então não exige novo teste unitário. Mesmo assim, após alterar o README, o grupo deve rodar:

```bash
mvn clean test
```

Para garantir que nenhuma alteração acidental foi feita no código.

---

## Commits sugeridos

```bash
git add README.md
git commit -m "docs: atualiza descricao do fluxo cliente-servidor"
```

Se a seção de protocolo for adicionada separadamente:

```bash
git add README.md
git commit -m "docs: documenta protocolo de comunicacao do minikahoot"
```

Se a seção de SMTP/Jenkins for adicionada separadamente:

```bash
git add README.md
git commit -m "docs: documenta configuracao de email no jenkins"
```

---

## Evidência esperada

- README atualizado.
- Fluxo cliente-servidor explicado corretamente.
- Protocolo documentado.
- Notificação por e-mail documentada.
- Melhorias futuras registradas.
- Commit de documentação no histórico.

---

# PARTE 2 — Configuração de SMTP no Jenkins para envio de e-mail

**Responsável:** Integrante 2  
**Foco:** configurar o Jenkins para que o bloco `mail` do `Jenkinsfile` funcione corretamente.

## Problema identificado

O `Jenkinsfile` possui envio de e-mail usando o passo `mail`.

Isso é válido, mas só funciona se o Jenkins tiver SMTP configurado. Caso contrário, pode acontecer o seguinte:

```text
Código compila
↓
Testes passam
↓
Package é gerado
↓
Relatórios são publicados
↓
Pipeline falha apenas porque o e-mail não foi enviado
```

Neste guia, a decisão é **manter a notificação por e-mail** e configurar o Jenkins corretamente.

---

## Objetivo da melhoria

Configurar o Jenkins para enviar notificações por e-mail ao final do pipeline, mantendo o bloco `mail` no `Jenkinsfile`.

A configuração deve permitir:

- Enviar e-mail quando o build passar.
- Enviar e-mail quando o build falhar.
- Manter relatórios JUnit publicados.
- Manter artefato `.jar` arquivado.
- Demonstrar na defesa que o Jenkins possui notificação configurada.

---

## Arquivos envolvidos

```text
Jenkinsfile
README.md
```

Também será necessário configurar o Jenkins pela interface web:

```text
Manage Jenkins → System → E-mail Notification
```

---

## Pré-requisitos

Antes de configurar o SMTP, conferir:

- Jenkins está rodando.
- Projeto já executa pipeline.
- Plugin `Mailer` está instalado.
- O Jenkins possui acesso à internet.
- Existe uma conta de e-mail para envio das notificações.
- Para Gmail, a conta deve ter verificação em duas etapas ativada e uma senha de app gerada.

---

## Passo 1 — Verificar plugin de e-mail

No Jenkins:

```text
Manage Jenkins
↓
Plugins
↓
Installed plugins
```

Procurar por:

```text
Mailer Plugin
```

Se não estiver instalado:

```text
Manage Jenkins
↓
Plugins
↓
Available plugins
↓
Pesquisar: Mailer
↓
Install
```

Depois reiniciar o Jenkins, se for solicitado.

---

## Passo 2 — Configurar e-mail do administrador

No Jenkins:

```text
Manage Jenkins
↓
System
↓
Jenkins Location
```

Preencher:

```text
System Admin e-mail address: seu_email@gmail.com
```

Exemplo:

```text
System Admin e-mail address: samuralise21@gmail.com
```

> O ideal é usar um e-mail do grupo ou um e-mail criado somente para o Jenkins.

---

## Passo 3 — Configurar SMTP no Jenkins

No Jenkins:

```text
Manage Jenkins
↓
System
↓
E-mail Notification
```

Para Gmail, usar como referência:

```text
SMTP server: smtp.gmail.com
Default user e-mail suffix: @gmail.com
Use SMTP Authentication: marcado
User Name: seu_email@gmail.com
Password: senha de app do Gmail
Use SSL: marcado
SMTP Port: 465
Reply-To Address: seu_email@gmail.com
Charset: UTF-8
```

Também é possível usar TLS, dependendo do ambiente:

```text
SMTP server: smtp.gmail.com
Use SMTP Authentication: marcado
User Name: seu_email@gmail.com
Password: senha de app do Gmail
Use TLS: marcado
SMTP Port: 587
Charset: UTF-8
```

> Se usar Gmail, não utilizar a senha normal da conta. Utilizar uma senha de app.

---

## Passo 4 — Criar senha de app no Gmail

No Google Account:

```text
Conta Google
↓
Segurança
↓
Como você faz login no Google
↓
Verificação em duas etapas
↓
Senhas de app
```

Criar uma senha de app para o Jenkins.

Depois copiar a senha gerada e usar no campo `Password` do Jenkins.

Observações importantes:

- A senha de app normalmente possui 16 caracteres.
- Ela deve ser usada no Jenkins no lugar da senha normal da conta.
- Guardar essa senha com cuidado.
- Não colocar a senha no GitHub.
- Não colocar a senha no `Jenkinsfile`.
- Não colocar a senha no README.

---

## Passo 5 — Testar envio de e-mail no Jenkins

Ainda na seção:

```text
Manage Jenkins → System → E-mail Notification
```

Marcar ou abrir a opção:

```text
Test configuration by sending test e-mail
```

Inserir o e-mail de destino e clicar em testar.

Resultado esperado:

```text
E-mail de teste enviado com sucesso.
```

Se falhar, verificar:

| Erro provável | Correção |
|---|---|
| Autenticação falhou | Usar senha de app, não senha normal |
| SMTP não conecta | Conferir porta 465 ou 587 |
| Jenkins sem internet | Testar rede do container |
| Plugin ausente | Instalar Mailer Plugin |
| Remetente inválido | Conferir System Admin e-mail address |
| Gmail bloqueando acesso | Confirmar verificação em duas etapas e senha de app |

---

## Passo 6 — Conferir o Jenkinsfile

Manter um bloco `post` com `mail`.

Modelo simples recomendado:

```groovy
post {
    success {
        mail to: 'destinatario@gmail.com',
             subject: 'MiniKahoot - Build aprovado',
             body: '''
Pipeline executado com sucesso.

Build, testes, package, relatorios JUnit e artefato foram gerados.
'''
    }

    failure {
        mail to: 'destinatario@gmail.com',
             subject: 'MiniKahoot - Build falhou',
             body: '''
Pipeline falhou.

Verifique o Console Output do Jenkins para identificar o erro.
'''
    }

    always {
        echo 'Execucao do pipeline finalizada.'
    }
}
```

Substituir `destinatario@gmail.com` pelo e-mail real que deve receber as notificações.

---

## Modelo recomendado de pipeline com e-mail

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    stages {
        stage('Ambiente') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Testes') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Artefatos') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            mail to: 'destinatario@gmail.com',
                 subject: 'MiniKahoot - Build aprovado',
                 body: 'Pipeline executado com sucesso. Testes e package foram finalizados.'
        }

        failure {
            mail to: 'destinatario@gmail.com',
                 subject: 'MiniKahoot - Build falhou',
                 body: 'Pipeline falhou. Verifique o Console Output do Jenkins.'
        }

        always {
            echo 'Execucao do pipeline finalizada.'
        }
    }
}
```

> Observação: se o Jenkins do grupo não usa configuração global de `tools`, pode ser necessário remover o bloco `tools` ou ajustar os nomes `Maven` e `JDK17` conforme cadastrados no Jenkins.

---

## Passo 7 — Testar pipeline completo

Depois de configurar SMTP e conferir o `Jenkinsfile`:

```bash
git add Jenkinsfile README.md
git commit -m "ci: configura notificacao por email no jenkins"
git push
```

No Jenkins, executar o job e validar:

```text
Build passou
Testes passaram
Relatório JUnit publicado
Artefato .jar arquivado
E-mail de sucesso recebido
```

Também testar um cenário de falha, se possível, em uma branch separada ou alteração temporária, apenas para verificar o e-mail de erro.

---

## Cuidados de segurança

Nunca versionar senhas.

Não colocar no GitHub:

```text
Senha normal do Gmail
Senha de app do Gmail
Token de SMTP
Credenciais do Jenkins
```

A senha deve ficar apenas na configuração do Jenkins.

Se precisar registrar evidência para a defesa, usar prints que escondam a senha.

---

## Commits sugeridos

Se só atualizar o `Jenkinsfile` mantendo `mail`:

```bash
git add Jenkinsfile
git commit -m "ci: ajusta notificacoes por email no pipeline jenkins"
```

Se também atualizar o README com instruções de SMTP:

```bash
git add README.md
git commit -m "docs: adiciona guia de smtp para notificacoes do jenkins"
```

Se fizer os dois juntos:

```bash
git add Jenkinsfile README.md
git commit -m "ci: configura notificacao por email no jenkins"
```

---

## Evidência esperada

- Plugin `Mailer` instalado.
- SMTP configurado no Jenkins.
- Teste de e-mail enviado com sucesso.
- Jenkinsfile mantendo `mail` no bloco `post`.
- Pipeline executando build, testes e package.
- Relatórios JUnit publicados.
- Artefato `.jar` arquivado.
- E-mail recebido em caso de sucesso ou falha.
- README documentando como configurar SMTP.

---

# PARTE 3 — Limpeza da estrutura, arquivos gerados e evidências finais

**Responsável:** Integrante 3  
**Foco:** organização do repositório e preparação para entrega.

## Problema identificado

O ZIP revisado continha itens que não deveriam ir para uma entrega limpa:

```text
.git/
target/
minikahoot.zip interno
.github/modernize/
estrutura duplicada minikahoot/minikahoot
```

Esses itens não necessariamente quebram o código, mas deixam a entrega menos profissional e podem confundir professor/colegas durante a avaliação.

---

## Objetivo da melhoria

Organizar o repositório para que a entrega contenha apenas o necessário:

- Código-fonte.
- Testes.
- `pom.xml`.
- Jenkinsfile.
- Dockerfile do Jenkins.
- docker-compose.
- README.
- Documentação necessária.

E remover/ignorar:

- `target/`.
- ZIPs internos.
- arquivos temporários.
- configurações locais desnecessárias.
- duplicações de pasta.

---

## Arquivos envolvidos

```text
.gitignore
README.md
estrutura de pastas do projeto
```

---

## `.gitignore` recomendado

Criar ou atualizar o arquivo `.gitignore` com:

```gitignore
# Maven / Java
target/
*.class
*.jar
*.war
*.ear

# IDEs
.idea/
.vscode/
*.iml

# Sistema operacional
.DS_Store
Thumbs.db

# Logs
*.log

# Arquivos compactados gerados
*.zip
*.rar
*.7z

# Arquivos temporarios
*.tmp
*.bak
```

---

## Remover arquivos gerados do controle de versão

Se `target/` já foi versionado, remover apenas do Git, sem apagar localmente:

```bash
git rm -r --cached target
```

Se existirem ZIPs internos versionados:

```bash
git rm --cached minikahoot.zip
```

Se existir pasta `.github/modernize/` e ela não for usada pelo grupo:

```bash
git rm -r --cached .github/modernize
```

> Atenção: usar `--cached` remove do controle de versão, mas mantém o arquivo localmente. Se quiser apagar do computador também, use `rm` ou exclua manualmente.

---

## Conferir estrutura final

A raiz do projeto deve ficar parecida com:

```text
minikahoot/
├── Dockerfile.jenkins
├── Jenkinsfile
├── README.md
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── src/
    ├── main/
    │   └── java/
    │       └── br/
    │           └── com/
    │               └── kahoot/
    │                   ├── BancoDePerguntas.java
    │                   ├── Cliente.java
    │                   ├── GerenciadorDePontos.java
    │                   ├── Pergunta.java
    │                   ├── Servidor.java
    │                   └── ServidorService.java
    └── test/
        └── java/
            └── br/
                └── com/
                    └── kahoot/
                        ├── BancoDePerguntasTest.java
                        ├── GerenciadorDePontosTest.java
                        ├── PerguntaTest.java
                        └── ServidorServiceTest.java
```

---

## Validar testes e package

Depois da limpeza, rodar:

```bash
mvn clean test
mvn clean package
```

Resultado esperado:

```text
BUILD SUCCESS
Testes executados sem falhas
Arquivo .jar gerado em target/
```

---

## Gerar ZIP limpo para entrega

Para gerar um ZIP sem arquivos desnecessários:

```bash
zip -r minikahoot_entrega.zip . \
  -x "target/*" \
  -x ".git/*" \
  -x "*.zip" \
  -x ".github/modernize/*"
```

No Windows PowerShell, uma alternativa simples é:

```powershell
Compress-Archive -Path Dockerfile.jenkins,Jenkinsfile,README.md,docker-compose.yml,pom.xml,src,.gitignore -DestinationPath minikahoot_entrega.zip -Force
```

---

## Commits sugeridos

Criar ou atualizar `.gitignore`:

```bash
git add .gitignore
git commit -m "chore: atualiza gitignore do projeto java maven"
```

Remover `target/` do versionamento:

```bash
git rm -r --cached target
git commit -m "chore: remove arquivos gerados do controle de versao"
```

Remover ZIPs internos:

```bash
git rm --cached minikahoot.zip
git commit -m "chore: remove arquivo zip interno do repositorio"
```

Se houver ajustes finais no README com evidências:

```bash
git add README.md
git commit -m "docs: adiciona evidencias finais de execucao e testes"
```

Commit final de organização:

```bash
git status
git add .
git commit -m "chore: organiza estrutura final para entrega"
```

---

## Evidência esperada

- `.gitignore` atualizado.
- `target/` fora do controle de versão.
- ZIPs internos removidos do repositório.
- Estrutura sem duplicidade desnecessária.
- `mvn clean test` passando.
- `mvn clean package` passando.
- ZIP final limpo gerado para entrega.

---

# 4. Checklist final das 3 partes

## Parte 1 — README

- [ ] README descreve corretamente `Servidor`.
- [ ] README descreve corretamente `Cliente`.
- [ ] README descreve corretamente `ServidorService`.
- [ ] README documenta o protocolo de mensagens.
- [ ] README explica como executar servidor e cliente.
- [ ] README explica como rodar testes.
- [ ] README registra melhorias futuras.
- [ ] README explica que o Jenkins usa notificação por e-mail via SMTP.

## Parte 2 — Jenkins com SMTP

- [ ] Plugin `Mailer` instalado.
- [ ] `System Admin e-mail address` preenchido.
- [ ] SMTP configurado em `Manage Jenkins → System`.
- [ ] Senha de app criada, se for Gmail.
- [ ] Teste de envio de e-mail feito pela interface do Jenkins.
- [ ] `Jenkinsfile` mantém bloco `mail`.
- [ ] Stage de testes executa `mvn test`.
- [ ] Relatórios JUnit são publicados.
- [ ] Artefato `.jar` é arquivado.
- [ ] Pipeline envia e-mail de sucesso ou falha.

## Parte 3 — Estrutura

- [ ] `.gitignore` atualizado.
- [ ] `target/` fora do versionamento.
- [ ] ZIPs internos fora do versionamento.
- [ ] Estrutura duplicada removida ou evitada na entrega.
- [ ] `mvn clean test` executado.
- [ ] `mvn clean package` executado.
- [ ] ZIP final limpo gerado.

---

# 5. Ordem recomendada de execução

```text
1. Atualizar README.md
2. Commit de documentação
3. Verificar plugin Mailer no Jenkins
4. Configurar System Admin e-mail address
5. Configurar SMTP no Jenkins
6. Criar senha de app, se for Gmail
7. Testar envio de e-mail pela interface do Jenkins
8. Ajustar Jenkinsfile mantendo o bloco mail
9. Commit de CI/CD
10. Atualizar .gitignore
11. Remover target/ e ZIPs do versionamento
12. Commit de limpeza
13. Rodar mvn clean test
14. Rodar mvn clean package
15. Validar Jenkins com e-mail
16. Gerar ZIP final limpo
17. Fazer merge para main, se tudo estiver aprovado
```

---

# 6. Sequência consolidada de commits

```bash
# Parte 1 — Documentação
git add README.md
git commit -m "docs: atualiza descricao do fluxo cliente-servidor"

git add README.md
git commit -m "docs: documenta protocolo de comunicacao do minikahoot"

git add README.md
git commit -m "docs: documenta configuracao de email no jenkins"

# Parte 2 — Jenkins com e-mail
git add Jenkinsfile
git commit -m "ci: ajusta notificacoes por email no pipeline jenkins"

# Parte 3 — Estrutura
git add .gitignore
git commit -m "chore: atualiza gitignore do projeto java maven"

git rm -r --cached target
git commit -m "chore: remove arquivos gerados do controle de versao"

# Se existir ZIP interno versionado
git rm --cached minikahoot.zip
git commit -m "chore: remove arquivo zip interno do repositorio"

# Validação final
mvn clean test
mvn clean package

git status
```

---

# 7. Como explicar na defesa

## Sobre o README

> O README foi atualizado após a integração final do cliente com o servidor, porque o fluxo evoluiu. Agora ele documenta corretamente o protocolo, a leitura de resposta, a validação, a pontuação e o encerramento.

## Sobre o Jenkins com e-mail

> Mantivemos a notificação por e-mail no Jenkins para demonstrar uma etapa adicional de CI/CD. Para isso, configuramos o SMTP na interface do Jenkins, usando o plugin de e-mail, e mantivemos o bloco `mail` no `Jenkinsfile`. As credenciais não foram versionadas no GitHub.

## Sobre a limpeza do repositório

> A limpeza removeu arquivos gerados pelo Maven, ZIPs internos e estruturas desnecessárias. Isso deixou o repositório mais claro, reproduzível e adequado para avaliação.

---

# 8. Conclusão

Com essas três partes aplicadas, o projeto fica mais coerente para entrega:

- O código implementado estará alinhado com a documentação.
- O Jenkins terá notificação por e-mail funcionando via SMTP.
- O repositório ficará limpo e organizado.
- As evidências de testes, artefatos e notificação ficarão mais fáceis de demonstrar.

Essas melhorias não mudam a regra principal do jogo, mas aumentam a qualidade da entrega, a maturidade do processo e a clareza para a defesa Q&A.

---

# 9. Referências úteis

- Jenkins Mailer Plugin: https://plugins.jenkins.io/mailer/
- Jenkins Email Extension Plugin: https://plugins.jenkins.io/email-ext/
- Google App Passwords: https://support.google.com/mail/answer/185833
