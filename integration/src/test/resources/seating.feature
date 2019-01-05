Feature: Randomly seat game players

  Scenario: seat 9 players and 0 dead stacks
    Given a game has 9 players
    When seating is done with 0 dead stacks
    Then 9 seats have been assigned

  Scenario: seat 11 players and 2 dead stacks
    Given a game has 11 players
    When seating is done with 2 dead stacks
    And the seated game is retrieved
    Then 13 seats have been assigned
