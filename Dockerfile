FROM eclipse-temurin:17-jdk-jammy

# Crear directorio de trabajo
WORKDIR /app

# Crear carpetas para archivos (deben existir antes de los volumenes)
RUN mkdir -p almacenamientoPolizaPDF almacenamientoPolizaPDFTemporal logs

# Copiar el JAR
ARG JAR_FILE=target/ipas-0.0.1.jar
COPY ${JAR_FILE} app_ipas.jar

EXPOSE 8080

# Arrancar con perfil de produccion
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app_ipas.jar"]
