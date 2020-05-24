# texastoc-v2
Refactor version 1 to version 2. Version 2 will be Spring Boot.

TDD (JUnit) for class/method testing employing mocking out external references.

BDD (Cucumber) for integration testing the endpoints using an embedded H2 database.

The only application code that was written was code to fix a failing test case (i.e. wrote the tests first).

# spring.profiles.active=dev
Setting the spring profile to "dev" will run the server with an H2 database with all tables created
and seeded. The tables and seed data can be found in the *create_toc_schema.sql* file.

To connect to the H2 server
* open `http://localhost:8080/h2-console` url.
* set the JDBC URL to `jdbc:h2:mem:testdb`
* User Name `sa`
* Leave the password empty
* Click Connect

# Run tests
You can run the tests in IntelliJ or from the command line.

To run in IntelliJ right click on the java folder and choose _Run 'All Tests'_
* application -> src -> test -> java

To run all the tests from the command line type
* mvn test

# Run the server
To run the server from the command line (note the default maven profile is "dev" and that will set the spring profile also to "dev")
* mvn -pl application spring-boot:run

To run the server from the command line (note the default maven profile is "dev" and that will set the spring profile also to "dev") and be able to attach a debugger on port 8787
* mvn -pl application spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8787"

To run the server in IntelliJ
* Run com.texastoc.Application by right clicking on it and click Run 'Application.main()'
* Stop the server and edit its run configuration and add -Dspring.profiles.active=dev as a VM option.
* Now you can run the Application (Run 'Application.main()') or in debug mode (Debug 'Application.main()')

# WebSocket
On branch 54-clock-web-socket added a websocket to the server. In the future this websocket
will be used to communicate a running clock to the client.

The client is going to first use polling so the websocket requirement has be put on hold.

# SSL certificate
Using LetsEncrypt for the SSL certificate.

To generate/renew

```
certbot certonly \
  --manual \
  --preferred-challenges=dns \
  --email <my email> \
  --agree-tos \
  --config-dir ./config \
  --logs-dir ./logs \
  --work-dir ./work \
  --cert-name texastoc.com \
  -d texastoc.com \
  -d www.texastoc.com
```

# Branches
The branch labels are prefixed in the order they were developer (e.g. 01-, 02, ...).

Choose the branch from the github list of branches to see the readme for that branch.

To see the code for branch compare the branch to the previous branch.

## Current Branch: 67-cache-season
Cache the current season.
