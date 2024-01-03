package com.dastan.spring.reactive.end2end;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/end2endTest/resources/features"},
        glue = {"com.dastan.spring.reactive.end2end.steps"}
)
public class CucumberTestRunner {

}
