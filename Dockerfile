# Usamos una imagen que tenga Java y los navegadores de Selenium
FROM selenium/standalone-chrome:latest

# Instalar Maven y otras herramientas necesarias
# Esto depende de si la imagen base de Selenium (basada en Debian) lo necesita
USER root
RUN apt-get update && apt-get install -y maven \ 
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app 

# Copia el código fuente (pom.xml, src/)
COPY . /app/ 

# Ya no necesitas el CMD, ya que lo sobrescribes en el Jenkinsfile con 'mvn clean test'
# CMD ["java", "-jar", "tests.jar"]
