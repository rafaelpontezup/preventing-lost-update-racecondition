##
# Builder Image
##
FROM maven:3.8.6-openjdk-18-slim AS builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

##
# Runner Image
##
FROM openjdk:17-alpine
COPY --from=builder /usr/src/app/target/*.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]