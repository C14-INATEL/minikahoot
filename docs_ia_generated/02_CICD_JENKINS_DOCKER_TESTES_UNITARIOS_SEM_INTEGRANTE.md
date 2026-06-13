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

# Guia 02 — Adicionar Job de Testes Unitários

## 1. Objetivo

Adicionar à pipeline o stage de **Testes Unitários**, mantendo tudo que já foi criado no Guia 01.

Depois desta etapa, a pipeline ficará assim:

```text
Ambiente
↓
Build Maven
↓
Testes Unitários
↓
Relatório JUnit
```

---

## 2. Arquivo alterado nesta etapa

Nesta etapa, alterar apenas:

```text
Jenkinsfile
```

Não é necessário alterar `Dockerfile.jenkins` nem `docker-compose.yml`.

---

## 3. Jenkinsfile após adicionar Testes Unitários

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
    }

    post {
        success {
            echo 'Pipeline executado com sucesso: build e testes aprovados.'
        }

        failure {
            echo 'Pipeline falhou. Verifique se os testes passaram localmente com mvn test.'
        }

        always {
            echo 'Execucao do pipeline finalizada.'
        }
    }
}
```

---

## 4. Por que publicar relatório JUnit?

O Maven Surefire gera relatórios dos testes em:

```text
target/surefire-reports/
```

O Jenkins lê esses arquivos com:

```groovy
junit 'target/surefire-reports/*.xml'
```

Isso atende à exigência de entregar relatórios de testes via CI/CD.

---

## 5. Testar localmente antes do commit

Antes de enviar para o GitHub, rodar na pasta do `pom.xml`:

```bash
mvn test
```

Se passar localmente, fazer commit.

---

## 6. Commit necessário

```bash
git checkout Backend
git pull origin Backend

git add Jenkinsfile
git commit -m "ci: adiciona stage de testes unitarios"

git push origin Backend
```

---

## 7. Resultado esperado no Jenkins

Após rodar o job, o Jenkins deve mostrar:

```text
Ambiente - Java Maven Git         SUCCESS
Build Maven        SUCCESS
Testes Unitarios   SUCCESS
```

Além disso, o Jenkins deve exibir os relatórios JUnit dos testes.

---

## 8. Evidências para defesa

Mostrar:

- Stage `Testes Unitarios`.
- Comando `mvn test` no console.
- Aba de testes do Jenkins.
- Relatórios JUnit publicados.
- Commit do integrante 2.

---

## 9. Perguntas prováveis

| Pergunta | Resposta esperada |
|---|---|
| Qual comando roda os testes? | `mvn test` |
| Onde ficam os relatórios? | `target/surefire-reports/*.xml` |
| Como o Jenkins lê esses relatórios? | Com `junit 'target/surefire-reports/*.xml'` |
| Esse job é de qual integrante? | Integrante 2 |

---

## 10. Checklist do Guia 02

- [ ] Rodar `mvn test` localmente.
- [ ] Atualizar `Jenkinsfile`.
- [ ] Adicionar stage `Testes Unitarios`.
- [ ] Adicionar publicação JUnit.
- [ ] Commitar alteração.
- [ ] Enviar para `Backend`.
- [ ] Executar Jenkins.
- [ ] Conferir relatórios de testes.