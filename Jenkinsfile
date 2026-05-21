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
<<<<<<< Updated upstream
=======
<<<<<<< HEAD

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
=======
>>>>>>> 7f1e0b033d7a99013b8172236f5e5971b7331613
>>>>>>> Stashed changes
    }

    post {
        success {
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
            echo 'Pipeline executado com sucesso: build e testes aprovados.'
        }

        failure {
            echo 'Pipeline falhou. Verifique se os testes passaram localmente com mvn test.'
        }

=======
>>>>>>> Stashed changes
            echo 'Pipeline inicial executado com sucesso: ambiente validado e build Maven aprovado.'
        }

        failure {
            echo 'Pipeline inicial falhou. Verifique o console do Jenkins.'
        }

        always {
            echo 'Execucao do Guia 01 finalizada.'
        }
<<<<<<< Updated upstream
=======
>>>>>>> 7f1e0b033d7a99013b8172236f5e5971b7331613
>>>>>>> Stashed changes
    }
}