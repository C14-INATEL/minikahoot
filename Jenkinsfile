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
