pipeline {
    agent any

    options {
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {
        stage('Validate Project') {
            steps {
                script {
                    sh '''
                        echo "=== Project Validation ==="
                        echo "Workspace: ${WORKSPACE}"
                        echo "Files:"
                        ls -la
                        echo ""
                        echo "pom.xml:"
                        [ -f "pom.xml" ] && echo "✓ Found" || { echo "✗ Missing!"; exit 1; }
                        echo ""
                        echo "Test files:"
                        find src/test -name "*.java" -type f 2>/dev/null | head -5 || echo "No test files found"
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build with explicit copying
                    sh '''
                        cat > Dockerfile << 'EOF'
FROM maven:3.9-eclipse-temurin-17

# Install Chrome
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get install -y ./google-chrome-stable_current_amd64.deb && \
    rm google-chrome-stable_current_amd64.deb && \
    apt-get clean

WORKDIR /workspace

# Chrome is installed, Maven is pre-installed
# We'll mount the workspace at runtime
EOF

                        docker build -t selenium-java-tests .
                    '''
                }
            }
        }

        stage('Run Tests with Mount') {
            steps {
                script {
                    sh '''
                        echo "=== Running Tests ==="
                        # Clean up
                        docker rm -f test-runner 2>/dev/null || true

                        # Run tests with workspace mounted
                        docker run --rm \
                            --name test-runner \
                            -v ${WORKSPACE}:/workspace \
                            -w /workspace \
                            --shm-size=2g \
                            selenium-java-tests \
                            bash -c "
                                echo 'Current directory: \$(pwd)'
                                echo 'Files in workspace:'
                                ls -la
                                echo ''
                                echo 'Running Maven tests...'
                                mvn clean test -DskipTests=false
                            " 2>&1 | tee test-output.log

                        echo ""
                        echo "=== Test Summary ==="
                        if grep -q "Tests run:" test-output.log; then
                            echo "SUCCESS: Tests executed!"
                            grep -A2 "Tests run:" test-output.log
                        else
                            echo "FAILURE: No tests executed"
                            echo "Last 30 lines:"
                            tail -30 test-output.log
                            # Don't fail yet, let's debug more
                        fi
                    '''
                }
            }
        }

        stage('Debug If Failed') {
            when {
                expression {
                    // Check if tests didn't run
                    return sh(script: 'grep -q "Tests run:" test-output.log 2>/dev/null || true', returnStatus: true) != 0
                }
            }
            steps {
                script {
                    sh '''
                        echo "=== DEBUG: Why no tests? ==="
                        echo "1. Checking if Maven can find project..."
                        docker run --rm \
                            -v ${WORKSPACE}:/workspace \
                            -w /workspace \
                            selenium-java-tests \
                            mvn help:evaluate -Dexpression=project.name -q -DforceStdout 2>&1 || echo "Maven error"

                        echo ""
                        echo "2. Checking test classes..."
                        docker run --rm \
                            -v ${WORKSPACE}:/workspace \
                            -w /workspace \
                            selenium-java-tests \
                            find . -name "*Test.java" -type f 2>/dev/null | head -10

                        echo ""
                        echo "3. Trying to compile tests..."
                        docker run --rm \
                            -v ${WORKSPACE}:/workspace \
                            -w /workspace \
                            selenium-java-tests \
                            mvn clean compile test-compile 2>&1 | tail -20
                    '''
                }
            }
        }

        stage('Publish Results') {
            steps {
                script {
                    sh '''
                        echo "=== Checking for Reports ==="
                        if [ -d "target/surefire-reports" ] && ls target/surefire-reports/*.xml 1>/dev/null 2>&1; then
                            echo "Found test reports:"
                            ls -la target/surefire-reports/
                            echo "Publishing..."
                        else
                            echo "WARNING: No test reports found"
                            echo "Creating test directory structure..."
                            mkdir -p target/surefire-reports
                        fi
                    '''

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