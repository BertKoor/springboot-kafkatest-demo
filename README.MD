# SpringBoot KafkaTest demo project

This is a simple SpringBoot project incorporating some Kafka, with tests.

## Build
Run the following command to build this Maven project, including reports:
````sh
mvn clean verify site
````
The test reports can be found in:

 * `./target/site/surefire.html` 
 * `./target/site/jacoco/index.html`

## Run
````sh
java -jar ./target/*.jar
````

Examples to call the REST endpoints with curl:

````sh
curl -v localhost:8080/api/hello/no
curl -v localhost:8080/api/hello/world
curl -v localhost:8080/api/hello -X PUT -H 'Content-Type: application/json' -d '{"hello": "no"}' 
curl -v localhost:8080/api/hello -X PUT -H 'Content-Type: application/json' -d '{"hello": "world"}'
````
When a body is PUT successfully (last example) then it will be published as an event on a Kafka topic.
This requires Kafka is already running.
