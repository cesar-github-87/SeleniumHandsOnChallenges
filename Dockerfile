# 1. ETAPA DE CONSTRUCCIÓN: Compilar el código Java
FROM maven:latest AS build
WORKDIR /app
# Copia el archivo pom.xml para descargar dependencias primero (mejora la caché)
COPY pom.xml .
# Descarga las dependencias (si hay cambios en el pom.xml)
RUN mvn dependency:go-offline
# Copia el código fuente
COPY src /app/src
# Compila y empaqueta el proyecto
RUN mvn clean package -DskipTests

# 2. ETAPA DE EJECUCIÓN: Entorno liviano con Java y un navegador
# Usamos una imagen que tiene Java y los navegadores de Selenium listos
FROM selenium/standalone-chrome:latest

# Instala el entorno Java Runtime Environment (JRE) necesario para correr el JAR
# Esta imagen base de Selenium ya incluye Java, pero si usaras una imagen base diferente,
# necesitarías un paso como: RUN apt-get update && apt-get install -y openjdk-17-jre

# Define el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR compilado desde la etapa 'build'
# Reemplaza 'nombre-de-tu-proyecto' con el nombre real de tu JAR
COPY --from=build /app/target/SeleniumOnHandsChallenges-1.0-SNAPSHOT.jar /app/tests.jar

# Establece el comando predeterminado para correr las pruebas.
# Nota: Tendrás que configurar tu JAR para que inicie la ejecución de pruebas al ejecutarse.
CMD ["java", "-jar", "tests.jar"]
