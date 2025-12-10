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
                  // Nota: Asegúrate de que tu Dockerfile tenga 'USER root' al final o no lo cambie
                    sh 'docker run --rm -w /app ' +
                       // 1. Montaje de Código: $PWD es el workspace local del agente
                       '-v $PWD:/app ' +
                       // 2. Montaje de la Cache M2: Usa $PWD para la cache en el host
                       '-v $PWD/m2-cache:/root/.m2 ' +
                       // 3. Montaje de Reportes:
                       '-v $PWD/target/surefire-reports:/app/target/surefire-reports ' +
                       'selenium-java-tests mvn clean test'
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
