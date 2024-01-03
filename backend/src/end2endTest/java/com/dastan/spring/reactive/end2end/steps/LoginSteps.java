package com.dastan.spring.reactive.end2end.steps;

import com.dastan.spring.reactive.common.LoginRequest;
import com.dastan.spring.reactive.config.SessionConfig;
import com.dastan.spring.reactive.end2end.ApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;


public class LoginSteps {
    ResponseEntity<Void> loginResponse;
    LoginRequest request;
    @Autowired
    private ApiClient apiClient;

    @Given("user with user {string} and password {string}")
    public void userWithUserAndPassword(String username, String password) {
        request = new LoginRequest(username, password);
    }

    @When("POST request to login endpoint is sent")
    public void postRequestToIsSent() {
        loginResponse = apiClient.login(request);
    }

    @Then("the response should be {int}")
    public void theResponseShouldBe(int expectedCode) {
        assertThat(loginResponse.getStatusCode().value()).isEqualTo(expectedCode);
    }

    @And("a token should be in the header")
    public void theATokenShouldBeInTheHeader() {
        assertThat(loginResponse.getHeaders().get(SessionConfig.XAUTHTOKEN).get(0)).isNotBlank();
    }
}
