# Message Board Service

This is the repository for a Message Board Service

To run with maven execute: `mvn spring-boot:run`

To build with maven and then run. Execute:

1. `mvn clean package`
2. `java -jar MessageBoardService-1.0.0-RELEASE.jar`

The project also has a build script that can be executed to build and expose the service in a docker container. 
This builds the projec,deploys a container and exposes port 8080 in the container on port 80 on the docker host. 

To execute the build script run: `sh build.sh`
