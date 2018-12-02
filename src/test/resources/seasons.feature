Feature: CRUD seasons
  Create, Retrieve, Update and Delete seasons

  Scenario: create a season
    Given season starts now
    When the season is created
    Then the start date should be now

  Scenario: create a season all minimal
    Given season starts now all else minimal
    When the season is created
    Then the start date should be now

  Scenario: validate season start required
    Given season start date is missing
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season kitty required
    Given season kitty per game is missing
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season kitty positive
    Given season kitty per game is negative
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season toc required
    Given season toc per game is missing
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season toc positive
    Given season toc per game is negative
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season quarterly toc required
    Given season quarterly toc per game is missing
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season quarterly toc positive
    Given season quarterly toc per game is negative
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season quarterly payouts required
    Given season quarterly num payouts is missing
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: validate season quarterly payouts one
    Given season quarterly num payouts is zero
    When the season is created
    Then response is "400 BAD_REQUEST"

  Scenario: get a season with no games
    Given season starts now
    When the season is created
    And the season is retrieved
    Then the season should have four quaters

