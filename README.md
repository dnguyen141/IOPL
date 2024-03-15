# IOP's Library

IOP's Library is the private library management for the Institute of Photonics, Leibniz University Hannover. This is the Java/Spring version of the original one, which was built using Python/Django.

## Features
- Authentication apis (log in/sign up/sign out/confirm registration per Email) with Json Web Tokens 
- User management apis
- Library management apis, including books and issues management
- Automatically schedule some library management tasks
- Swagger UI documentation

## API Documentation
- Swagger UI is available under http://localhost:8080/swagger-ui/index.html when the application starts in local machine. Screenshot:
  ![swagger-ui](https://i.imgur.com/lLqa3En.png)

## Tools
- Java 17
- Spring Framework 6
- Hibernate Validator
- Lombok
- PostgreSQL
- JUnit 5
- Mockito
- OpenAPI
- Apache POI
- SendGrid API
- Docker

## Running instruction

### Back-end
- **docker-compose**: Using the <em>docker-compose.yml</em> under <em>LibraryManagement</em> folder, the application can be started via docker-compose and reachable under http://localhost:8080:
  ```bash
  docker-compose up
  ```
- **local startup**: After cloning the project, you need to manually set up <em>application.yml</em> under <em>/src/main/resources</em>. 
The <em>application.yml</em> should look similar in the following code snippet:
  ```bash
  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/iopl
      username: <db-username>
      password: <db-password>
      driver-class-name: org.postgresql.Driver
    jpa:
      hibernate:
        ddl-auto: create
      show-sql: true
      properties:
        hibernate:
          format_sql: true
      database: postgresql
      database-platform: org.hibernate.dialect.PostgreSQLDialect
    docker:
      compose:
        enabled: true
    sendgrid:
      api-key: <sendgrid-api-key>
    servlet:
      multipart:
        max-file-size: 10MB
        max-request-size: 50MB

  application:
    security:
      jwt:
        secret-key: <jwt-secret-key>
        expiration: 900000 # 15 minutes
        refresh-token:
          expiration: 604800000 # 1 week

  springdoc:
    api-docs:
      enabled: true
      path: /api-docs
    swagger-ui:
      enabled: true
      tags-sorter: alpha
      operations-sorter: method
    show-actuator: true

  library:
    coversPath: ./src/main/resources/covers
  ```
  And then run the application:
  ```bash
    mvn spring-boot:run
  ```

### Front-end
To be determined. 

## Testing
- Use maven in <em>LibraryManagement</em> folder to run all the unit tests:
  ```
    mvn test
  ```