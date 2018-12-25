Feature: Add players to a game

  Scenario: create, add player no buy-in and retrieve game
    Given a game is created
    And a player is added without buy-in
    And the game is retrieved
    Then the retrieved game has one player no buy-in

  Scenario: create, add player with buy-in and retrieve game
    Given a game is created
    And a player is added with buy-in
    And the game is retrieved
    Then the retrieved game has one player with buy-in
