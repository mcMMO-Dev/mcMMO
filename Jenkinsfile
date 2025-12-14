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
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-deployer',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    configFileProvider([configFile(fileId: 'maven-settings-nexus', variable: 'MAVEN_SETTINGS_TEMPLATE')]) {
                        sh '''
                          # Expand env vars into a real settings file
                          envsubst < "$MAVEN_SETTINGS_TEMPLATE" > settings.xml

                          mvn -s settings.xml -V -B deploy

                          rm -f settings.xml
                        '''
                    }
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
