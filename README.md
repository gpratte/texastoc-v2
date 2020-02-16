# texastoc-v2
Refactor version 1 to version 2. Version 2 will be Spring Boot.

TDD (JUnit) for class/method testing employing mocking out external references.

BDD (Cucumber) for integration testing the endpoints using an embedded H2 database.

The only application code that was written was code to fix a failing test case (i.e. wrote the tests first).


# Run tests
You can run the tests in IntelliJ or from the command line.

To run in IntelliJ right click on the java folder and choose _Run 'All Tests'_
* application -> src -> test -> java
* integration -> src -> test -> java

To run all the tests from the command line type
* mvn test

The cucumber tests in the integration module run with
* @ActiveProfiles("test")


Setting **spring.profiles.active=true** results in creating an H2 database and runs the statements in the *create_toc_schema.sql* file for the tests are run.


# Run Server with H2 database
Set the following to run the server with an H2 database with all tables created:
* spring.profiles.active=test

Setting the **spring.profiles.active=true** results in creating an H2 database and runs the statements in the *create_toc_schema.sql* file.

To connect to the H2 server 
* open `http://localhost:8080/h2-console` url. 
* set the JDBC URL to `jdbc:h2:mem:testdb`
* User Name `sa`
* Leave the password empty
* Click Connect

# Branches

The branch labels are prefixed in the order they were developer (e.g. 01-, 02, ...).

Choose the branch from the github list of branches to see the readme for that branch.

## Current Branch: 39-housekeeping

It's been months since this project has seen any action while I've been learning react/redux.

This branch does a couple of things
* set the active profile to "test" when running the integration tests
* use Travis CI for continuous integration
