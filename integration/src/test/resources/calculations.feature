Feature: Finalizing a game results in calculations

  Scenario: a game with players is finalized
    Given a game has 10 players all finished
    When the game is finalized
    And the finalized game is retrieved
    Then the retrieved game is properly calculated

