# Stage de build
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copia pom.xml e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia arquivos de configuração
COPY checkstyle.xml .
COPY pmd-ruleset.xml .

# Copia código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Stage final
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia JAR compilado
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Expõe porta
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Executa com otimizações JVM
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
