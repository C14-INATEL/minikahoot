# MiniKahoot — CI/CD com Jenkins em Container Docker

**Projeto:** MiniKahoot  
**Disciplina:** C14 — Engenharia de Software  
**Branches:** `main` e `Backend`  
**Ferramenta de CI/CD:** Jenkins  
**Ambiente:** Jenkins em container Docker  
**Restrição atendida:** não usar GitHub Actions  
**Estratégia:** dividir a construção da pipeline em 4 entregas, cada uma com um stage/job associado a um integrante.

---

## Visão geral da estratégia

A pipeline será construída de forma incremental:

| Guia | Responsável | Entrega principal |
|---|---|---|
| Guia 01 | Integrante 1 | Base Docker/Jenkins + stage de Build Maven |
| Guia 02 | Integrante 2 | Stage de Testes Unitários + relatório JUnit |
| Guia 03 | Integrante 3 | Stage de Package JAR |
| Guia 04 | Integrante 4 | Stage de Relatórios, Artefatos e Notificação |

Essa divisão ajuda a atender ao requisito da NP2 de ter pelo menos um job/stage por integrante, com commits próprios e evidências claras, sem colocar o nome do integrante diretamente no nome do stage.

---

# Guia 04 — Relatórios, Artefatos e Notificação

## 1. Objetivo

Finalizar a pipeline com:

- Publicação de artefatos.
- Reforço dos relatórios JUnit.
- Validação final.
- Notificação de sucesso ou falha.

Depois desta etapa, a pipeline completa ficará assim:

```text
Ambiente
↓
Build Maven
↓
Testes Unitários
↓
Package JAR
↓
Relatórios e Artefatos
↓
Notificação
```

---

## 2. Arquivo alterado nesta etapa

Alterar apenas:

```text
Jenkinsfile
```

---

## 3. Jenkinsfile final completo

Substituir o conteúdo do `Jenkinsfile` pelo seguinte:

```groovy
pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Ambiente - Java Maven Git') {
            steps {
                echo 'Verificando ambiente do container Jenkins...'
                sh 'java -version'
                sh 'mvn -version'
                sh 'git --version'
            }
        }

        stage('Build Maven') {
            steps {
                echo 'Compilando o projeto MiniKahoot...'
                sh 'mvn clean compile'
            }
        }

        stage('Testes Unitarios') {
            steps {
                echo 'Executando testes unitarios...'
                sh 'mvn test'
            }
            post {
                always {
                    echo 'Publicando relatorios JUnit dos testes...'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package JAR') {
            steps {
                echo 'Gerando pacote JAR do MiniKahoot...'
                sh 'mvn clean package'
            }
        }

        stage('Relatorios e Artefatos') {
            steps {
                echo 'Arquivando artefato JAR...'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

                echo 'Garantindo publicacao dos relatorios JUnit...'
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Validacao Final') {
            steps {
                echo 'Pipeline completo executado.'
                echo 'Etapas: ambiente, build, testes, package, relatorios e artefatos.'
                echo "Branch: ${env.BRANCH_NAME}"
                echo "Build: ${env.BUILD_NUMBER}"
            }
        }
    }

    post {
        success {
            echo 'Notificacao: pipeline executado com sucesso.'

            mail to: 'email-do-grupo@exemplo.com',
                 subject: "MiniKahoot - Pipeline #${env.BUILD_NUMBER} executado com sucesso",
                 body: "O pipeline do MiniKahoot foi executado com sucesso. Build, testes, package e publicacao de artefatos passaram."
        }

        failure {
            echo 'Notificacao: pipeline falhou.'

            mail to: 'email-do-grupo@exemplo.com',
                 subject: "MiniKahoot - Pipeline #${env.BUILD_NUMBER} falhou",
                 body: "O pipeline do MiniKahoot falhou. Verifique o console do Jenkins para detalhes."
        }

        always {
            echo 'Execucao completa do pipeline finalizada.'
        }
    }
}
```

---

## 4. Caso o e-mail ainda não esteja configurado

O envio com `mail` depende da configuração SMTP do Jenkins.

Se o Jenkins ainda não estiver configurado para e-mail, usar temporariamente esta versão no bloco `post`:

```groovy
post {
    success {
        echo 'Notificacao: pipeline executado com sucesso.'
    }

    failure {
        echo 'Notificacao: pipeline falhou.'
    }

    always {
        echo 'Execucao completa do pipeline finalizada.'
    }
}
```

No README, documentar:

> A notificação por e-mail foi prevista no Jenkinsfile, mas depende da configuração SMTP do ambiente. Enquanto o SMTP não estiver configurado, a notificação visual do Jenkins e os logs do pipeline serão usados como evidência.

---

## 5. Por que arquivar artefatos?

O comando:

```groovy
archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
```

faz o Jenkins guardar o `.jar` gerado pelo Maven.

Isso representa a etapa de publicação/entrega do artefato.

Como o MiniKahoot não possui servidor de produção real, o grupo pode explicar:

> O deploy foi representado pela publicação do artefato `.jar` no Jenkins, permitindo baixar e executar a versão gerada em qualquer ambiente com Java 17.

---

## 6. Commit necessário

```bash
git checkout Backend
git pull origin Backend

git add Jenkinsfile
git commit -m "ci: adiciona publicacao de relatorios e artefatos"

git push origin Backend
```

Se também atualizar o README:

```bash
git add README.md
git commit -m "docs: documenta pipeline Jenkins com Docker"
git push origin Backend
```

---

## 7. Resultado esperado no Jenkins

Após rodar o job, o Jenkins deve mostrar:

```text
Ambiente - Java Maven Git                    SUCCESS
Build Maven                   SUCCESS
Testes Unitarios              SUCCESS
Package JAR                   SUCCESS
Relatorios e Artefatos        SUCCESS
Validacao Final                              SUCCESS
```

Também deve ser possível ver:

- Relatórios JUnit.
- Histórico de testes.
- Artefato `.jar` arquivado.
- Console output completo.
- Notificação ou mensagem de sucesso.

---

## 8. Evidências para defesa

Mostrar:

- `Jenkinsfile` final.
- Stage `Relatorios e Artefatos`.
- Relatórios JUnit.
- Artefato `.jar` arquivado.
- Histórico de builds.
- Commit do integrante 4.
- README explicando Jenkins em Docker.

---

## 9. Perguntas prováveis

| Pergunta | Resposta esperada |
|---|---|
| Como o Jenkins publica o `.jar`? | Com `archiveArtifacts artifacts: 'target/*.jar'` |
| Como os testes aparecem no Jenkins? | Com `junit 'target/surefire-reports/*.xml'` |
| Isso é deploy real? | Não, é publicação de artefato |
| Por que isso é válido? | Porque o projeto não tem ambiente de produção real |
| Esse stage é de qual integrante? | Integrante 4 |
| Por que usar Docker? | Para reproduzir o ambiente do Jenkins |

---

## 10. Checklist do Guia 04

- [ ] Atualizar `Jenkinsfile`.
- [ ] Adicionar stage `Relatorios e Artefatos`.
- [ ] Adicionar `archiveArtifacts`.
- [ ] Manter publicação JUnit.
- [ ] Adicionar validação final.
- [ ] Configurar ou simular notificação.
- [ ] Commitar alteração.
- [ ] Enviar para `Backend`.
- [ ] Executar Jenkins.
- [ ] Conferir artefato `.jar`.
- [ ] Conferir relatórios JUnit.
- [ ] Atualizar README, se necessário.