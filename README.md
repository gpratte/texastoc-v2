# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

# Branches

## branch 01-security-basic-auth
Enable basic authentication and CORS 

Module | Function
------------ | -------------
TexastocApplication.java | Added the security configuration to require basic authentication and allow cors
UserController.java | /user endpoint to return the principal

## branch 02-create-season

SeasonRestController POST endpoint to create a season. 

New SeasonService.

Junit test (followed TDD) for the controller that passes through to the service. No persistence yet.

## branch 03-create-season-repository

JdbcTemplate based SeasonRepository. Use the @MockBean for the SeasonServiceTest unit test.

## branch 04-cucumber-create-season
Added cucumber test. These tests bring up the server and use Spring's RestTempate to call the endpoint to create a season. Also uses an embedded H2 database. Uses a command line runner to create the season table.

## branch 05-tdd-get-season
Implement the GET method on the season service using TDD.

## branch 06-cucumber-get-season
Cucumber test to get a season after creating the season. Checks if there are four quarters.

## branch 07-tdd-create-game
Use TDD to code creating a new game.

## branch 08-cucumber-create-game
Cucumber test to create a game after creating a season. 


A lot of other refactoring, for example:
* use config database for season/game setttings
* create season only requires the start date (the other values are read from config database)

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
