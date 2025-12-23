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
                     // LIMPIEZA PREVENTIVA: Asegura que podemos crear el contenedor con el nombre
                     sh 'docker rm -f maven-runner || true'
                     sh 'mkdir -p target/surefire-reports'

                     // 1. CREAR EL CONTENEDOR (en estado Created)
                     sh 'docker create --name maven-runner -w /app selenium-java-tests tail -f /dev/null'

                     // 2. COPIAR el workspace (código, pom.xml, src/) al contenedor. ESTO RESUELVE EL ERROR POM.
                     sh 'docker cp $WORKSPACE/. maven-runner:/app'

                     // 3. INICIAR EL CONTENEDOR
                     sh 'docker start maven-runner'

                     // 4. EJECUTAR MAVEN DENTRO del contenedor
                     // Usamos 'sh -c' para que el shell interno del contenedor ejecute el comando completo
                     sh 'docker exec maven-runner sh -c "mvn clean test"'

                     // 5. COPIAR los reportes DE VUELTA al workspace
                     sh 'docker cp maven-runner:/app/target/surefire-reports $WORKSPACE/target/'

                     // 6. LIMPIAR (Eliminar el contenedor)
                     sh 'docker rm -f maven-runner'
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
                    junit '**/target/surefire-reports/*.xml'

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
