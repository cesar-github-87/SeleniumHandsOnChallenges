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
                    // Asegura que el directorio de reportes exista en el HOST
                    sh 'mkdir -p target/surefire-reports'

                    sh '''
                        echo "=== Running Tests ==="

                        docker run --rm \
                            --shm-size=2g \
                            -u root \
                            -w /app \

                            # 1. MONTAJE COMPLETO DEL CÓDIGO Y POM.XML
                            -v ${WORKSPACE}:/app \

                            # 2. MONTAJE DEL CACHÉ DE MAVEN
                            -v ${WORKSPACE}/m2-cache:/root/.m2 \

                            # 3. MONTAJE ESPECÍFICO DEL DIRECTORIO DE REPORTES
                            # Esto asegura que Surefire escriba los reportes al Host.
                            -v ${WORKSPACE}/target/surefire-reports:/app/target/surefire-reports \

                            selenium-java-tests \

                            # Ejecuta Maven de forma limpia y ejecuta las pruebas
                            mvn clean test
                    '''
                }
            }
        }
/*
        stage('Run Tests') {
            steps {
                script {
                    sh 'mkdir -p target/surefire-reports'

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
        }*/

        // REMOVED 'Get Results' STAGE - It is no longer needed because of the volume mount
        stage('Publish Results') {
            steps {
                script {
                    // 1. CORRECCIÓN CLAVE: Asegurar permisos de lectura/escritura (777)
                    // Esto permite que el usuario de Jenkins lea los archivos creados por el usuario 'root' de Docker.
                    sh 'chmod -R 777 target/surefire-reports'

                    // 2. Comprobación (Opcional, pero útil)
                    sh 'ls -la target/surefire-reports/ || echo "Directory not found"'

                    // 3. Publicación de Resultados
                    junit 'target/surefire-reports/*.xml'

                    // 4. LIMPIEZA FINAL (Asegurar que el contenedor temporal se elimine si Run Tests falló antes de la limpieza)
                    // Nota: Este paso debe estar en 'post' o en 'Run Tests', pero lo dejamos aquí para asegurar.
                    sh 'docker rm -f maven-runner || true'
                }
            }
        }
        /*stage('Publish Results') {
            steps {
                script {
                    sh 'ls -la target/surefire-reports/ || echo "Directory not found"'
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }*/

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