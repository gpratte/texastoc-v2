# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

# Run tests
In IntelliJ the JUnit tests in the application module run with no extra configuration.

In IntelliJ the cucumber tests in the integration module require the following spring profile to be set:
* spring.profiles.active=test

To run all the tests from the command line type:
* mvn -Dspring.profiles.active=true test

Setting the **spring.profiles.active=true** results in creating an H2 database and runs the statements in the *create_toc_schema.sql* file.


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

## branch 14-game-with-one-player-no-buy-in

No buy-in means no payouts.
