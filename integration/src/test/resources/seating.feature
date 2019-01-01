Feature: Randomly seat game players

  Scenario: get seats before seating
    Given a game has 12 players
    When seating is done with 3 dead stacks
    Then all 15 seats have been assigned

