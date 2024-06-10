package com.training_microservice.dao;

import com.training_microservice.domain.documents.TrainerSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerSummaryRepo extends MongoRepository<TrainerSummary, String> {
}
