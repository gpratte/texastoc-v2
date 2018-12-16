# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

# Run tests
For cucumber tests set the following environment variable:
* spring.profiles.active=test

# Run Server with H2 database
Set the following environment variable:
* spring.profiles.active=test

Also comment out 
`<scope>test</scope>`
for the H2 dependency in the pom.xml

To connect to the H2 server 
* open `http://localhost:8080/h2-console` url. 
* set the JDBC URL to `jdbc:h2:mem:testdb`
* User Name `sa`
* Leave the password empty
* Click Connect

# Branches

The branch labels are prefixed in the order they were developer (e.g. 01-, 02, ...).

See the readme for the branch for more information about it.

## branch 10-get-game

Code the get game service using TDD and then test the GET game endpoint using cucumber.