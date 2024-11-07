# Use uma imagem base do Maven para compilar o projeto
FROM maven:3.8.5-openjdk-17 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de configuração do Maven e o código-fonte para o contêiner
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o arquivo JAR
RUN mvn clean package

# Use uma imagem base do OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR gerado pelo Maven para o diretório de trabalho
COPY --from=build /app/target/exemplo_network-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que a aplicação Spring Boot irá rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]