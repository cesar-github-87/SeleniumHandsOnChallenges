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
                    // Clean up previous container
                    sh 'docker rm -f selenium-test-runner || true'

                    // Clean target directory
                    sh 'rm -rf target && mkdir -p target'

                    echo "Running Selenium tests in Docker container..."

                    // Run tests with explicit output
                    sh '''
                        docker run --name selenium-test-runner \
                            -v ${WORKSPACE}:/app \
                            -w /app \
                            --shm-size=2g \
                            selenium-java-tests \
                            mvn clean test -X 2>&1 | tee maven-output.log
                    '''

                    // Check the output
                    sh '''
                        echo "=== Checking Maven output ==="
                        grep -A5 -B5 "Tests run:" maven-output.log || echo "No test summary found"
                        grep -i "BUILD" maven-output.log || echo "No build status found"
                    '''
                }
            }
        }

        stage('Verify Reports') {
            steps {
                script {
                    sh '''
                        echo "=== Looking for test reports ==="
                        echo "Current directory: $(pwd)"
                        echo ""
                        echo "All files in workspace:"
                        ls -la
                        echo ""
                        echo "Target directory contents:"
                        ls -la target/ 2>/dev/null || echo "No target directory"
                        echo ""
                        echo "Searching for XML files:"
                        find . -name "*.xml" -type f 2>/dev/null | grep -v ".git" | head -20
                        echo ""
                        echo "Checking surefire-reports directory:"
                        ls -la target/surefire-reports/ 2>/dev/null || echo "No surefire-reports directory"
                        echo ""
                        echo "If no reports, checking if tests compiled:"
                        find . -name "*Test.class" -type f 2>/dev/null | head -5
                    '''
                }
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    // Try multiple possible locations
                    echo "Attempting to publish test reports..."

                    // Method 1: Standard location
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true

                    // Method 2: Any XML in target
                    junit testResults: 'target/*.xml', allowEmptyResults: true

                    // Method 3: Search recursively
                    junit testResults: '**/TEST-*.xml', allowEmptyResults: true
                }
            }
        }
    }

    post {
        always {
            script {
                // Clean up
                sh 'docker rm -f selenium-test-runner || true'
                sh 'docker system prune -f'

                // Archive everything for debugging
                archiveArtifacts artifacts: 'maven-output.log', allowEmptyArchive: true
                archiveArtifacts artifacts: 'target/**/*', allowEmptyArchive: true

                // Archive test reports if they exist
                sh '''
                    if ls target/surefire-reports/*.xml 2>/dev/null; then
                        echo "Test reports found, archiving..."
                    else
                        echo "No test reports to archive"
                    fi
                '''
            }
        }
    }
}