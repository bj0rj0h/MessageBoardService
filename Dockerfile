FROM openjdk:8-jre-alpine
LABEL maintainer="bjorn.j@outlook.com"

COPY target/MessageBoardService-1.0.0-Release.jar /srv/
EXPOSE 8080/tcp

CMD java -jar /srv/MessageBoardService-1.0.0-Release.jar
