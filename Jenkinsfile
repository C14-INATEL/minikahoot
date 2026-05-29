groovy
pipeline {
    agent any

    environment {
        EMAIL_DESTINO = 'luis.cortes@ges.inatel.br'
    }

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

            mail to: EMAIL_DESTINO,
                 subject: "MiniKahoot - Pipeline #${env.BUILD_NUMBER} executado com sucesso",
                 body: "O pipeline do MiniKahoot foi executado com sucesso. Build, testes, package e publicacao de artefatos passaram."
        }

        failure {
            echo 'Notificacao: pipeline falhou.'

            mail to: EMAIL_DESTINO,
                 subject: "MiniKahoot - Pipeline #${env.BUILD_NUMBER} falhou",
                 body: "O pipeline do MiniKahoot falhou. Verifique o console do Jenkins para detalhes."
        }
        always {
            echo 'Execucao completa do pipeline finalizada.'
        }
    }
}

