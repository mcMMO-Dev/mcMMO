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
			when {
				branch 'master'
			}
			steps {
				withCredentials([usernamePassword(
					credentialsId: 'nexus-deployer',
					usernameVariable: 'NEXUS_USER',
					passwordVariable: 'NEXUS_PASS'
				)]) {
					writeFile file: 'settings.xml', text: """
                    <settings>
                      <servers>
                        <server>
                          <id>neetgames</id>
                          <username>${env.NEXUS_USER}</username>
                          <password>${env.NEXUS_PASS}</password>
                        </server>
                      </servers>
                    </settings>
                    """
					sh 'mvn -s settings.xml -V -B deploy'
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
