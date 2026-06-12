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

# Guia 01 — Base do Jenkins em Docker + Build Maven

## 1. Objetivo

Criar a estrutura inicial de CI/CD com Jenkins rodando em container Docker.

Nesta primeira etapa, a pipeline terá apenas:

```text
Ambiente
↓
Build Maven
```

Ainda não serão adicionados os stages de testes, package, relatórios ou artefatos. Eles entram nos próximos guias.

---

## 2. Arquivos criados nesta etapa

```text
minikahoot/
├── Dockerfile.jenkins
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
└── src/
```

---

## 3. Dockerfile.jenkins

Criar na raiz do projeto:

```text
Dockerfile.jenkins
```

Conteúdo:

```dockerfile
FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && apt-get install -y \
    maven \
    git \
    docker.io \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

USER jenkins
```

### Explicação

| Item | Função |
|---|---|
| `jenkins/jenkins:lts-jdk17` | Jenkins LTS já com JDK 17 |
| `maven` | Necessário para build do projeto |
| `git` | Necessário para checkout do repositório |
| `docker.io` | Permite evolução futura para uso de Docker no pipeline |
| `USER jenkins` | Volta para usuário padrão do Jenkins |

---

## 4. docker-compose.yml

Criar na raiz do projeto:

```text
docker-compose.yml
```

Conteúdo:

```yaml
services:
  jenkins:
    build:
      context: .
      dockerfile: Dockerfile.jenkins
    container_name: minikahoot-jenkins
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      - jenkins_home:/var/jenkins_home
      - /var/run/docker.sock:/var/run/docker.sock
    restart: unless-stopped

volumes:
  jenkins_home:
```

### Explicação

| Item | Função |
|---|---|
| `8080:8080` | Acesso ao Jenkins pelo navegador |
| `50000:50000` | Porta para agentes Jenkins |
| `jenkins_home` | Mantém configurações e histórico |
| `docker.sock` | Permite integração com Docker, se necessário |
| `restart` | Reinicia o container automaticamente |

---

## 5. Jenkinsfile — apenas Build Maven

Criar na raiz do projeto:

```text
Jenkinsfile
```

Conteúdo inicial:

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
    }

    post {
        success {
            echo 'Pipeline inicial executado com sucesso: ambiente validado e build Maven aprovado.'
        }

        failure {
            echo 'Pipeline inicial falhou. Verifique o console do Jenkins.'
        }

        always {
            echo 'Execucao do Guia 01 finalizada.'
        }
    }
}
```

---

## 6. Como subir o Jenkins

Na pasta onde estão `Dockerfile.jenkins` e `docker-compose.yml`, executar:

```bash
docker compose up -d --build
```

Verificar o container:

```bash
docker ps
```

Acessar:

```text
http://localhost:8080
```

Pegar a senha inicial:

```bash
docker exec -it minikahoot-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

---

## 7. Criar o job no Jenkins

Criar um job do tipo **Pipeline from SCM**:

| Campo | Valor |
|---|---|
| Nome | `MiniKahoot-CI-CD` |
| Tipo | Pipeline |
| SCM | Git |
| Branch | `*/Backend` |
| Script Path | `Jenkinsfile` |

Depois clicar em:

```text
Build Now
```

---

## 8. Resultado esperado

Ao final desta etapa, o Jenkins deve mostrar:

```text
Ambiente - Java Maven Git    SUCCESS
Build Maven   SUCCESS
```

O objetivo é provar que:

- O Jenkins subiu em Docker.
- O Jenkins acessou o repositório.
- Java 17 está disponível.
- Maven está disponível.
- O projeto compila.

---

## 9. Commits necessários

### Commit 1 — Dockerfile

```bash
git checkout Backend
git pull origin Backend

    git add Dockerfile.jenkins
git commit -m "ci: adiciona Dockerfile do Jenkins"
```

### Commit 2 — docker-compose

```bash
git add docker-compose.yml
git commit -m "ci: adiciona docker compose para Jenkins"
```

### Commit 3 — Jenkinsfile inicial com Build Maven

```bash
git add Jenkinsfile
git commit -m "ci: adiciona stage de build Maven"
```

### Push

```bash
git push origin Backend
```

---

## 10. Evidências para defesa

Mostrar:

- `Dockerfile.jenkins`.
- `docker-compose.yml`.
- `Jenkinsfile` com stage de build.
- Container `minikahoot-jenkins` rodando.
- Job do Jenkins executado.
- Console mostrando `mvn clean compile`.

---

## 11. Checklist do Guia 01

- [ ] Criar `Dockerfile.jenkins`.
- [ ] Criar `docker-compose.yml`.
- [ ] Criar `Jenkinsfile` inicial.
- [ ] Subir Jenkins com Docker.
- [ ] Acessar Jenkins em `localhost:8080`.
- [ ] Criar job `MiniKahoot-CI-CD`.
- [ ] Executar pipeline.
- [ ] Validar stage de ambiente.
- [ ] Validar stage de Build Maven.
- [ ] Fazer commits e push na branch `Backend`.