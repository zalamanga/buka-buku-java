FROM maven:3.9-eclipse-temurin-17 AS build
ENV MAVEN_OPTS="-Xmx384m -Djava.net.preferIPv4Stack=true"
WORKDIR /app
COPY pom.xml ./
COPY .mvn ./.mvn
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q verify

FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/buka-buku.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
