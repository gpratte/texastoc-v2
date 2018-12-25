Feature: Add players to a game

  Scenario: create game, add player no buy-in and retrieve game
    Given a game is created
    And a player is added without buy-in
    And the game is retrieved
    Then the retrieved game has one player no buy-in

  Scenario: create game, add player with buy-in and retrieve game
    Given a game is created
    And a player is added with buy-in
    And the game is retrieved
    Then the retrieved game has one player with buy-in

  Scenario: create game, add 2 players with buy-in and retrieve game
    Given a game is created
    And two players are added with buy-in
    And the game is retrieved
    Then the retrieved game has two players with buy-in

  Scenario: create game, add random players and retrieve game
    Given a game is created
    And random players are added
    And the game is retrieved
    Then the retrieved game has random players
