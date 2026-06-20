FROM maven:3.9-eclipse-temurin-8 AS build

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates gnupg \
    && curl -fsSL https://deb.nodesource.com/setup_16.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/*

ARG BUILD_MODE=dev

COPY pom.xml package.json ./
COPY docker/maven-settings.xml /root/.m2/settings.xml
COPY docker/web-dev.xml ./docker/web-dev.xml
COPY frontend ./frontend
COPY src ./src
COPY docker/dblocal.dev.properties ./src/main/resources/dblocal.properties

RUN if [ "$BUILD_MODE" = "dev" ]; then \
      cp docker/web-dev.xml src/main/webapp/WEB-INF/web.xml; \
    fi \
    && mvn clean package -Pproduction -DskipTests -B

FROM tomcat:9.0-jdk8-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/siacoes-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
