Feature: CRUD Games
  Create, Retrieve, Update (start, end) and Delete games

  Scenario: create a game
    Given season starts now
    And the game starts now
    When the game is created
    Then the game belongs to the first quarter

