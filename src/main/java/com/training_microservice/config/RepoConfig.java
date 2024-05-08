package com.training_microservice.config;

import com.training_microservice.dao.TrainingRepo;
import com.training_microservice.dao.TrainingRepository;
import com.training_microservice.dao.inmemory.TrainingStorageInMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.training_microservice")
public class RepoConfig {

    @Bean
    @ConditionalOnProperty(name = "app.repository", havingValue = "jpa")
    public TrainingRepo jpaTrainingRepoBean(TrainingRepository trainingRepository) {
        return trainingRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "app.repository", havingValue = "in-memory")
    public TrainingRepo inMemoryTrainingRepoBean() {
        return new TrainingStorageInMemory();
    }

}
