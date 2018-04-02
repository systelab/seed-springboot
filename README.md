# Spring Boot Example

## Getting Started

To get you started you can simply clone the `springboot-test` repository and install the dependencies:

### Prerequisites

You need [git][git] to clone the `springboot-test` repository.

You will need [Java™ SE Development Kit 8][jdk-download] and [Maven][maven].

### Clone `springboot-test`

Clone the `springboot-test` repository using git:

```bash
git clone https://github.com/systelab/springboot-test.git
cd seed-jee
```

### Install Dependencies

In order to install the dependencies and generate the Uber jar you must run:

```bash
mvn clean install
```

### Run

To launch the server, simply run with java -jar the generated jar file.

```bash
java -jar springboot-test-1.0-SNAPSHOT.jar
```

## API

You will find the swagger UI at http://localhost:8080/swagger-ui.html


[git]: https://git-scm.com/
[maven]: https://maven.apache.org/download.cgi
[jdk-download]: http://www.oracle.com/technetwork/java/javase/downloads


