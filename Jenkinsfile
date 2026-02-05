pipeline {
	agent any

	tools {
		jdk 'jdk17'
		// If you configured Maven as a Jenkins tool, add:
		// maven 'Maven3'
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
				sh 'mvn -V -B clean package'
			}
		}

        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings-nexus', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s "$MAVEN_SETTINGS" -V -B deploy'
                }
            }
        }

	}

	post {
		success {
			archiveArtifacts artifacts: 'target/mcMMO.jar', fingerprint: true
		}
	}
}
