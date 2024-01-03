# The backend of the sample project which uses Spring Boot Reactive with mongoDB
### 

The tests are based on:
* simple unit tests with mocking
* integration tests with testcontainter
* end2end tests with cucumber

Before running the cucumber tests inside `src/end2endTest` you have to start the mongoDB docker container with: `docker-compose up`  

### Guides for the used techstack
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

