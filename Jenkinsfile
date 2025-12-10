pipeline {
    agent any

    options {
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t selenium-java-tests .'
                }
            }
        }

        stage('Run Selenium Tests') {
            steps {
                script {
                    // Define container name directly in the script block
                    def containerName = "maven-runner-${BUILD_ID}"

                    sh "docker rm -f ${containerName} || true"
                    sh 'mkdir -p target/surefire-reports'

                    sh """
                        docker run --name ${containerName} \
                            -v ${WORKSPACE}/target:/app/target \
                            -w /app \
                            selenium-java-tests \
                            mvn clean test || echo "Tests completed with exit code: \$?"
                    """
                }
            }
        }

        stage('Debug Container') {
            when {
                anyOf {
                    expression { currentBuild.result == null }
                    expression { currentBuild.result == 'FAILURE' }
                    expression { currentBuild.result == 'UNSTABLE' }
                }
            }
            steps {
                script {
                    def containerName = "maven-runner-${BUILD_ID}"

                    echo "=== DEBUGGING: Checking container and report locations ==="

                    sh """
                        echo "1. Checking if container is still running..."
                        docker ps -a --filter "name=${containerName}" || true

                        echo "\\n2. Checking workspace structure on HOST..."
                        ls -la ${WORKSPACE}/ || true
                        echo "\\nTarget directory:"
                        ls -la ${WORKSPACE}/target/ || true

                        echo "\\n3. Checking container filesystem (if container exists)..."
                        if docker ps -a --format '{{.Names}}' | grep -q "${containerName}"; then
                            echo "Container exists, checking contents..."
                            docker exec ${containerName} ls -la /app/ || echo "Cannot exec in container"
                            echo "\\nSearching for test reports in container:"
                            docker exec ${containerName} find /app -type f -name "*.xml" 2>/dev/null || echo "No XML files found"
                        else
                            echo "Container ${containerName} does not exist or was removed"
                        fi

                        echo "\\n4. Checking for any generated XML files on HOST..."
                        find ${WORKSPACE} -name "*.xml" -type f 2>/dev/null | head -20 || echo "No XML files found"
                    """
                }
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    sh """
                        echo "Looking for JUnit reports..."
                        find ${WORKSPACE} -name "TEST-*.xml" -type f | head -5
                    """

                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }

    post {
        always {
            script {
                def containerName = "maven-runner-${BUILD_ID}"
                sh "docker rm -f ${containerName} || true"
                sh 'docker system prune -f'
                archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                echo "Build Status: ${currentBuild.currentResult}"
            }
        }
    }
}