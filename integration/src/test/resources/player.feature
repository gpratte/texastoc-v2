Feature: Update Player

  Scenario: Update password
    Given a new player
    When the player password is updated
    And the player is retrieved
    Then the player has the expected encoded password

