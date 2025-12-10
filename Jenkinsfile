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
                    // 1. Asegurar que el directorio de reportes exista en el HOST
                 sh 'mkdir -p target/surefire-reports'
                    
                    // 2. CREAMOS UN CONTENEDOR NUEVO (sin volúmenes externos conflictivos)
                    sh 'docker create --name maven-runner -w /app selenium-java-tests tail -f /dev/null'
                    
                    // 3. COPIAMOS el workspace de Jenkins AL contenedor
                    sh 'docker cp $WORKSPACE/. maven-runner:/app'
                    
                    // 4. EJECUTAMOS MAVEN DENTRO del contenedor
                    sh 'docker exec maven-runner mvn clean test'
                    
                    // 5. COPIAMOS los reportes DE VUELTA al workspace
                    sh 'docker cp maven-runner:/app/target/surefire-reports $WORKSPACE/target/'
                    
                    // 6. DETENER y ELIMINAR el contenedor temporal
                    sh 'docker rm -f maven-runner'
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
