# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

# Run tests
For cucumber tests set the following environment variable:
* spring.profiles.active=test

# Run Server with H2 database
Set the following environment variable:
* spring.profiles.active=test

To connect to the H2 server 
* open `http://localhost:8080/h2-console` url. 
* set the JDBC URL to `jdbc:h2:mem:testdb`
* User Name `sa`
* Leave the password empty
* Click Connect

# Branches

The branch labels are prefixed in the order they were developer (e.g. 01-, 02, ...).

Choose the branch from the github list of branches to see the readme for that branch.

## branch 11-application-integration-modules

Moved the application (and the JUnit tests) into an *application* module/folder.

Moved the cucumber tests into an *integration* module/folder.

Exposed the test classes from the application module to the integration module. See http://maven.apache.org/guides/mini/guide-attached-tests.html

Created a *this-and-that* folder for non-code related files. Move the *architecture* folder and the *tools* folder into the *this-and-that* folder.
