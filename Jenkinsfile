pipeline {
    // 1. Ejecutar el pipeline en cualquier agente de Jenkins que tenga el cliente Docker
    agent any

    options {
        // Establece un límite de tiempo para evitar que el job se quede colgado
        timeout(time: 60, unit: 'MINUTES')
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    // La imagen de Docker es construida usando el Dockerfile que definiste.
                    // Esto compila el código Java y crea la imagen 'selenium-java-tests'.
                    sh 'docker build -t selenium-java-tests .'
                }
            }
        }

       stage('Run Selenium Tests') {
           steps {
               script {
                   // Clean up
                   sh "docker rm -f ${CONTAINER_NAME} || true"

                   // Create target directory
                   sh 'mkdir -p target/surefire-reports'

                   try {
                       // Create and start container
                       sh "docker run -d --name ${CONTAINER_NAME} -w /app selenium-java-tests tail -f /dev/null"

                       // Copy current workspace to container
                       sh "docker cp ${WORKSPACE}/. ${CONTAINER_NAME}:/app/"

                       // Run tests with verbose output
                       sh "docker exec ${CONTAINER_NAME} mvn clean test -X"

                       // Wait a moment for files to be written
                       sleep 5

                       // Copy reports back - with multiple fallback options
                       sh """
                           # Try to copy reports
                           docker cp ${CONTAINER_NAME}:/app/target/surefire-reports ${WORKSPACE}/target/ 2>/dev/null || \
                           echo "Primary reports directory not found, trying alternatives..."

                           # Try alternative locations
                           docker cp ${CONTAINER_NAME}:/target/surefire-reports ${WORKSPACE}/target/ 2>/dev/null || \
                           docker cp ${CONTAINER_NAME}:/surefire-reports ${WORKSPACE}/target/ 2>/dev/null || \
                           echo "Could not find test reports"

                           # List what was actually generated in container
                           docker exec ${CONTAINER_NAME} find / -name "TEST-*.xml" 2>/dev/null | head -20
                       """

                   } catch (Exception e) {
                       echo "Error during test execution: ${e.toString()}"

                       // Try to get container logs for debugging
                       sh "docker logs ${CONTAINER_NAME} || true"

                       // Try to salvage any existing reports
                       sh """
                           docker cp ${CONTAINER_NAME}:/app ${WORKSPACE}/container-content/ 2>/dev/null || true
                           find ${WORKSPACE} -name "*.xml" -type f | head -10
                       """

                       // Don't fail the stage yet, let the report publishing handle it
                       currentBuild.result = 'UNSTABLE'
                   }
               }
           }
       }

        stage('Publish Reports') {
            steps {
                script {
                    // Lee los reportes de JUnit generados por Maven/Surefire dentro de la carpeta 'target'
                    // y publica los resultados en la interfaz de Jenkins.
                    // La ruta es relativa al workspace, donde el Bind Mount escribió el archivo.
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }
    }

    post {
        always {
            // Limpia las capas de caché no utilizadas para ahorrar espacio en disco.
            // Esto es crucial en entornos CI/CD para mantener el agente limpio.
            sh 'docker system prune -f'
        }
    }
}
