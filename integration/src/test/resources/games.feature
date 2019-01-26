Feature: CRUD Games
  Create, Retrieve, Update (start, end) and Delete games

  Scenario: create a simple game
    Given admin user logs in
    Given the game starts now
    When the game is created
    Then the game is normal
    Then the game is not double buy in nor transport required

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

  Scenario: create and update a simple game
    Given the game starts now
    When the game is created and retrieved
    And the retrieved game is updated and retrieved
    Then the game is normal
    Then the game is double buy-in, transport and delta changed
