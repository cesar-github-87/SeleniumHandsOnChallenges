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
                    ''' +
                    // Comando docker run EN UNA SOLA CADENA LARGA (más seguro)
                    'docker run --rm --shm-size=2g -u root -w /app ' +
                    '-v ${WORKSPACE}:/app ' +
                    '-v ${WORKSPACE}/m2-cache:/root/.m2 ' +
                    '-v ${WORKSPACE}/target/surefire-reports:/app/target/surefire-reports ' +
                    'selenium-java-tests ' + // El nombre de la imagen debe ir aquí
                    'mvn clean test'
                }
            }
        }

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
