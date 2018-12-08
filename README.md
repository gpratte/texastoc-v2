# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

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

## branch 04-bdd-cucumber
Added cucumber test. These tests bring up the server and use Spring's RestTempate to call the endpoint to create a season. Also uses an embedded H2 database. Uses a command line runner to create the season table.

## branch 05-tdd-get-season
Implement the GET method on the season service using TDD.

## branch 06-cucumber-get-season
Cucumber test to get a season after creating the season. Checks if there are four quarters.
