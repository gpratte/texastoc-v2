# texastoc-v2
Refactor version 1. Version 2 will be spring boot and Angular.

## branch 01-gradle
Using gradle for build.

## branch 02-security-basic-auth
Enable basic authentication and CORS 

Module | Function
------------ | -------------
TexastocApplication.java | Added the security configuration to require basic authentication and allow cors
UserController.java | /user endpoint to return the principal

Merged 02-security-basic-auth into master.

