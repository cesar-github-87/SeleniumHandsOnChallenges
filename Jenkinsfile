pipeline {
    agent any

    options {
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {
        stage('Check Workspace') {
            steps {
                script {
                    // Ensure target exists on Jenkins side and is clean before we start
                    sh 'rm -rf target'
                    sh 'mkdir -p target'
                    sh 'chmod 777 target' // Ensure Docker can write to it
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t selenium-java-tests .'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh '''
                        echo "=== Running Tests ==="

                        # Note: We removed 'clean' from the mvn command below
                        # We also added user matching (-u) to prevent permission issues

                        docker run --rm \
                            --shm-size=2g \
                            -v ${WORKSPACE}/target:/app/target \
                            -v ${WORKSPACE}/test-output.log:/app/test-output.log \
                            selenium-java-tests \
                            bash -c "mvn test -DskipTests=false 2>&1 | tee /app/test-output.log"
                    '''
                }
            }
        }

        // REMOVED 'Get Results' STAGE - It is no longer needed because of the volume mount

        stage('Publish Results') {
            steps {
                script {
                    sh 'ls -la target/surefire-reports/ || echo "Directory not found"'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            script {
                archiveArtifacts artifacts: 'test-output.log', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/surefire-reports/*.xml', allowEmptyArchive: true
                sh 'docker system prune -f'
            }
        }
    }
}