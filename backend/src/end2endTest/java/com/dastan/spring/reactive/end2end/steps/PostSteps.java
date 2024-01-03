package com.dastan.spring.reactive.end2end.steps;

import com.dastan.spring.reactive.common.PaginatedResult;
import com.dastan.spring.reactive.end2end.ApiClient;
import com.dastan.spring.reactive.posts.persistence.PostsRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class PostSteps {

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private ApiClient apiClient;
    private ResponseEntity<PaginatedResult> response;


    @Given("{int} post entries in the DB")
    public void postEntriesInTheDB(int count) {
        IntStream.range(0, count).forEach(a -> postsRepository.create("mypost " + UUID.randomUUID(), "content_" + UUID.randomUUID()).block());
    }

    @When("when posts endpoint with query parameter {string} is called")
    public void whenPostsEndpointWithQueryParameterIsCalled(String queryParam) {
        response = apiClient.queryPosts(queryParam, 0, 10);
    }

    @Then("the api response should be {int}")
    public void theApiResponseShouldBe(int code) {
        assertThat(response.getStatusCode().value()).isEqualTo(code);
    }

    @And("response should contain expected {int} post entries")
    public void responseShouldContainExpectedPostData(int count) {
        final PaginatedResult body = response.getBody();
        assertThat(body.data().size()).isEqualTo(count);
    }


}
