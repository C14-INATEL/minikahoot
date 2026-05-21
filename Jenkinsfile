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

            echo 'Pipeline inicial executado com sucesso: ambiente validado e build Maven aprovado.'
        }

        failure {
            echo 'Pipeline inicial falhou. Verifique o console do Jenkins.'
        }
    }
}
