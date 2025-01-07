# SpringBoot KafkaTest demo project

This is a simple SpringBoot project incorporating some Kafka functionality with an integration tests.

It is created to demonstrate an issue with the logging of a warning during the execution of tests, 
caused by test artifacts owned by Apache Kafka.

## Reproducing the issue
* Edit the file `pom.xml` and change the value of property `spring-boot.version` to `3.3.2` or higher
* Run the command `mvn test` which will log the same warning multiple times:
````text
org.junit.platform.launcher.core.LauncherConfigurationParameters loadClasspathResource
WARNING: Discovered 2 'junit-platform.properties' configuration files in the classpath; only the first will be used.
````
* When `spring-boot.version` is set to `3.4.0` or higher, the repeated logging is:
````text
org.junit.platform.launcher.core.LauncherConfigurationParameters loadClasspathResource
WARNING: Discovered 3 'junit-platform.properties' configuration files on the classpath (see below); only the first (*) will be used.
- jar:file:~/.m2/repository/org/apache/kafka/kafka-clients/3.8.1/kafka-clients-3.8.1-test.jar!/junit-platform.properties (*)
- jar:file:~/.m2/repository/org/apache/kafka/kafka-server-common/3.8.1/kafka-server-common-3.8.1-test.jar!/junit-platform.properties
- jar:file:~/.m2/repository/org/apache/kafka/kafka_2.13/3.8.1/kafka_2.13-3.8.1-test.jar!/junit-platform.properties
````
* Additionally, a test will fail:
````text
[INFO] Running nl.bertkoor.springboot.kafkatest.demo.JunitPlatformPropertiesOnClasspathTest
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.007 s <<< FAILURE! -- in nl.bertkoor.springboot.kafkatest.demo.JunitPlatformPropertiesOnClasspathTest
[ERROR] nl.bertkoor.springboot.kafkatest.demo.JunitPlatformPropertiesOnClasspathTest.detectIssue -- Time elapsed: 0.003 s <<< FAILURE!
org.opentest4j.AssertionFailedError: there should be at most one 'junit-platform.properties' on the classpath ==> expected: <1> but was: <2>
````

## Background

### Issue tracking references
* SpringBoot issue [41446](https://github.com/spring-projects/spring-boot/issues/41446), opened on 11 Jul 2024
* Apache Kafka issue [17121](https://issues.apache.org/jira/browse/KAFKA-17121), opened on 11 Jul 2024 as well

### History
The issue surfaced with Apache Kafka version 3.7.1, got somewhat worse with 3.8.0
but eventually got fixed with version Apache Kafka version 3.9.0 which was released on 30 Oct 2024.

### Cause Analysis
For a SpringBoot application, all that is needed to be affected by this issue, 
is a dependency on artifact `org.springframework.kafka:spring-kafka-test`.
Via a couple of transitive dependencies there will be multiple (although identical)
copies present on the classpath of `junit-platform.properties` 
which will cause JUnit-Jupiter to give multiple warnings.

The dependency tree is similar for Spring Boot versions 3.3.2 up to 3.3.6:
````text
+- org.springframework.boot:spring-boot-starter-test:jar:3.3.2:test
\- org.springframework.kafka:spring-kafka-test:jar:3.2.2:test
   +- org.apache.kafka:kafka-clients:jar:test:3.7.1:test
   +- org.apache.kafka:kafka-server-common:jar:3.7.1:test
   +- org.apache.kafka:kafka-server-common:jar:test:3.7.1:test
   +- org.apache.kafka:kafka_2.13:jar:3.7.1:test
   \- org.apache.kafka:kafka_2.13:jar:test:3.7.1:test
````

### How to deal with the issue
It is impossible (or rather highly impractical) with Maven to filter out the offending files from the transitive dependencies.

It has been tried to exclude the offending jars from `spring-kafka-test`, but all three jars contain essential classes.

The logging by JUnit is delegated to `java.util.logging.Logger`, which alas is relatively hard to influence.

The easiest fix is to keep the Spring Boot version on `3.3.1` or lower, 
and wait until the Spring Boot project has upgraded its dependency on Apache Kafka to version `3.9.0` or higher.
As I'm writing this, it has been [committed](https://github.com/spring-projects/spring-boot/issues/43676)
and is scheduled to be released under SpringBoot `3.5.0-M1`.

Another (rather dirty) fix is to put these fragments in your `pom.xml` to override the jar versions:
````xml
<properties>
    <kafkatest.version>3.7.0</kafkatest.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <classifier>test</classifier>
            <version>${kafkatest.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-server-common</artifactId>
            <classifier>test</classifier>
            <version>${kafkatest.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.13</artifactId>
            <classifier>test</classifier>
            <version>${kafkatest.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
````

## Build this project
Run the following command to build this Maven project, including reports:
````sh
mvn clean verify site
````
The test reports can be found in:

 * `./target/site/surefire.html` 
 * `./target/site/jacoco/index.html`

## Run this project
Out of scope for the demonstration, but it can be run via the following command:
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
This requires Kafka is running and listening on `localhost:9092`.
