#!/bin/sh

mvn clean package
cp target/test-1.0-SNAPSHOT.jar .
docker build -t bjorjoh-messageboard:v1 .
docker run --name message_board -p 8080:8080 -d bjorjoh-messageboard:v1
