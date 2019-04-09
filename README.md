# Message Board Service

This is the repository for a Message Board Service

To run with maven execute: `mvn spring-boot:run`

To build with maven and then run. Execute:

1. `mvn clean package`
2. `java -jar MessageBoardService-1.0.0-RELEASE.jar`

The project also has a build script that can be executed to build and expose the service in a docker container. 
This builds the projec,deploys a container and exposes port 8080 in the container on port 80 on the docker host. 

To execute the build script run: `sh build.sh`

To see the logs of the container run: `docker logs -f {name_of_container}`

## Usage instructions

To use the service a valid JWT token will have to be provided as a bearer token in the authorization header. 
One of the following tokens can be used to authenticate against the service. The email at the end of each token is the user represented. Do NOT include the email when passing the token. 

`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJqb2huRG9lQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiSm9obiIsIlN1cm5hbWUiOiJEb2UiLCJFbWFpbCI6ImpvaG5Eb2VAZXhhbXBsZS5jb20ifQ.zJNqbnp5drRdADn-BgbauC5XpzlvSSRkA8KVwrbuQ98`  johnDoe@example.com

`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJqYW5lRG9lQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiSmFuZSIsIlN1cm5hbWUiOiJEb2UiLCJFbWFpbCI6ImphbmVEb2VAZXhhbXBsZS5jb20ifQ.gPCx0722ufOkDPwd5qTdqGRn5I9eaFXYGdfNYFm9DWc`  janeDoe@example.com

`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJhbmRlcnMuc3ZlbnNzb25AZXhhbXBsZS5jb20iLCJHaXZlbk5hbWUiOiJBbmRlcnMiLCJTdXJuYW1lIjoiU3ZlbnNzb24iLCJFbWFpbCI6ImFuZGVycy5zdmVuc3NvbkBleGFtcGxlLmNvbSJ9.ANT3gsfq7m6Dleoc4fhnK8szOoQ9_BBCPZci9w3wedk` anders.svensson@example.com

`eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoLmJqb3Jqb2guc2UiLCJpYXQiOjE1NTQ1NDU1OTQsImV4cCI6MTU4NjA4MTU5NCwiYXVkIjoibWVzc2FnZUJvYXJkLmJqb3Jqb2guc2UiLCJzdWIiOiJsaXNhLmVyaWtzc29uQGV4YW1wbGUuY29tIiwiR2l2ZW5OYW1lIjoiTGlzYSIsIlN1cm5hbWUiOiJFcmlrc3NvbiIsIkVtYWlsIjoibGlzYS5lcmlrc3NvbkBleGFtcGxlLmNvbSJ9.aH7qdF99fPraVD5WW6s3Om6Yl7xUOuIPV0tEj4AMFV4` lisa.eriksson@example.com
