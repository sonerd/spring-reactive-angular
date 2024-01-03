Feature: API post feature

  Scenario: Retrieve posts by query parameter
    Given 2 post entries in the DB
    When when posts endpoint with query parameter "mypost" is called
    Then the api response should be 200
    And response should contain expected 2 post entries