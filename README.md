# Bank Details Service

## About
This is a Spring Boot Microservice for storage of users bank details against their claim.

### Prerequisites

* Java 8
* Maven
* Docker (for postgresql)

## DB
### Local DB

The easiest way to have a local DB up and running on your machine is to use docker
```bash
$ docker run --name dwp-jsa -e POSTGRES_PASSWORD=password -e POSTGRES_DB=dwp-jsa -p5432:5432 postgres
```

