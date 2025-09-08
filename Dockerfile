# Etapa 1: Build
FROM openjdk:19-jdk AS build
WORKDIR /app

# Copiar el pom y el código fuente
COPY pom.xml .
COPY src src

# Copiar Maven Wrapper
COPY mvnw .
COPY .mvn .mvn

# Dar permisos al wrapper y compilar
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM openjdk:17-jdk
VOLUME /tmp

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]

# Exponer puerto 8080
EXPOSE 8080
