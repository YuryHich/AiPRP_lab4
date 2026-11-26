# Сборка WAR внутри контейнера: на компьютере не нужны ни Maven, ни JDK
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# Tomcat 9 (javax), как в ЛР4
FROM tomcat:9.0-jdk17
ENV TZ=Europe/Minsk
COPY --from=build /build/target/TimeService.war /usr/local/tomcat/webapps/TimeService.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
