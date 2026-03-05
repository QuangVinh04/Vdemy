#FROM openjdk:21
#
#ARG FILE_JAR=target/*.jar
#
#ADD ${FILE_JAR} api-service.jar
#
#ENTRYPOINT ["java","-jar","api-service.jar"]
#
#EXPOSE 8081


# Stage 1: Build JAR
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run App
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]