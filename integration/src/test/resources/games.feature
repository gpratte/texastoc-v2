Feature: CRUD Games
  Create, Retrieve, Update (start, end) and Delete games

  Scenario: create a simple game
    Given the game starts now
    When the game is created
    Then the game is normal

  Scenario: create a double buy in game
    Given the double buy in game starts now
    When the game is created
    Then the game is double buy in

  Scenario: game requires transport supplies
    Given the game supplies need to be moved
    When the game is created
    Then the game transport supplies flag is set

  Scenario: create and retrieve a simple game
    Given the game starts now
    When the game is created and retrieved
    Then the retrieved game is normal
    Then the retrieved game has no players

