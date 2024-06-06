package com.training_microservice.cucumber.config;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
@CucumberOptions(
        features = {"src/test/resources"},
        plugin = {"pretty"},
        glue = {"com.training_microservice.cucumber.TrainingServiceSteps"})
public class CucumberSpringConfiguration {


}
