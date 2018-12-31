Feature: CRD Supplies
  Create, Retrieve, and Delete games

  Scenario: create a supply
    Given chairs have been bought
    When the supply is created
    Then the supplies are retrieved
    Then then supply is in the list

