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