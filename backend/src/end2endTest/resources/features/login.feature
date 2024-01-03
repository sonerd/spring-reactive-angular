Feature: API login feature

  Scenario: Login user
    Given user with user "user" and password "password"
    When POST request to login endpoint is sent
    Then the response should be 200
    And a token should be in the header