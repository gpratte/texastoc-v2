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
 
