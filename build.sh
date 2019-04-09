#!/bin/sh

mvn clean package
cp target/MessageBoardService-1.0.0-Release.jar .
docker build -t bjorjoh-messageboard:v1 .
docker run --name message_board -p 8080:8080 -d bjorjoh-messageboard:v1
rm MessageBoardService-1.0.0-Release.jar
