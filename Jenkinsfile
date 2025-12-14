pipeline {
    agent any

    tools {
        jdk 'jdk17'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
                    mvn -V -B clean package
                '''
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/mcMMO.jar', fingerprint: true
        }
    }

}
