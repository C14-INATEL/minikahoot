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

# Guia 03 — Adicionar Job de Package JAR

## 1. Objetivo

Adicionar à pipeline o stage de **Package JAR**, mantendo os stages anteriores.

Depois desta etapa, a pipeline ficará assim:

```text
Ambiente
↓
Build Maven
↓
Testes Unitários
↓
Package JAR
```

O objetivo é gerar o pacote `.jar` do MiniKahoot após os testes passarem.

---

## 2. Arquivo alterado nesta etapa

Alterar apenas:

```text
Jenkinsfile
```

---

## 3. Jenkinsfile após adicionar Package JAR

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
    }

    post {
        success {
            echo 'Pipeline executado com sucesso: build, testes e package aprovados.'
        }

        failure {
            echo 'Pipeline falhou. Verifique build, testes ou package.'
        }

        always {
            echo 'Execucao do Guia 03 finalizada.'
        }
    }
}
```

---

## 4. Por que usar `mvn clean package`?

O comando:

```bash
mvn clean package
```

faz:

1. Limpa a pasta `target`.
2. Compila o projeto.
3. Executa os testes.
4. Gera o pacote final do projeto.

O `.jar` fica na pasta:

```text
target/
```

---

## 5. Testar localmente antes do commit

Na pasta onde está o `pom.xml`, rodar:

```bash
mvn clean package
```

Se o `.jar` for gerado em `target/`, o stage está coerente.

---

## 6. Commit necessário

```bash
git checkout Backend
git pull origin Backend

git add Jenkinsfile
git commit -m "ci: adiciona stage de package jar"

git push origin Backend
```

---

## 7. Resultado esperado no Jenkins

Após rodar o job, o Jenkins deve mostrar:

```text
Ambiente - Java Maven Git         SUCCESS
Build Maven        SUCCESS
Testes Unitarios   SUCCESS
Package JAR        SUCCESS
```

Nesta etapa o `.jar` é gerado, mas ainda não é arquivado como artefato. Isso será feito no Guia 04.

---

## 8. Evidências para defesa

Mostrar:

- Stage `Package JAR`.
- Comando `mvn clean package`.
- Console mostrando `BUILD SUCCESS`.
- Pasta `target/` com `.jar`.
- Commit do integrante 3.

---

## 9. Perguntas prováveis

| Pergunta | Resposta esperada |
|---|---|
| Qual comando gera o `.jar`? | `mvn clean package` |
| Onde o `.jar` é gerado? | Na pasta `target/` |
| Por que package vem depois dos testes? | Para só gerar entrega quando os testes passam |
| Esse stage é de qual integrante? | Integrante 3 |

---

## 10. Checklist do Guia 03

- [ ] Rodar `mvn clean package` localmente.
- [ ] Atualizar `Jenkinsfile`.
- [ ] Adicionar stage `Package JAR`.
- [ ] Commitar alteração.
- [ ] Enviar para `Backend`.
- [ ] Executar Jenkins.
- [ ] Conferir `BUILD SUCCESS`.
- [ ] Conferir geração do `.jar`.