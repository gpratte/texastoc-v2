Feature: Player features

  Scenario: Update password
    Given a new player
    When the player password is updated
    And the player is retrieved
    Then the player has the expected encoded password

  Scenario: Login
    Given a new player with email and password
    When the player logs in
    Then a token is returned

