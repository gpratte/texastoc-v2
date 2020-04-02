Feature: Randomly seat game players

  Scenario: seat 9 players and 0 dead stacks
    Given a game has 9 players
    When seating is done with 9 and 0 seats
    And the seated game is retrieved
    Then 9 players are seated at table 1
    And table 1 has 0 dead stacks

#  Scenario: seat 11 players and 2 dead stacks
#    Given a game has 11 players
#    When seating is done with 7 and 6 seats
#    And the seated game is retrieved
#    Then 6 players are seated at table 1
#    Then 5 players are seated at table 2
#    And table 1 has one dead stack
#    And table 2 has one dead stack
