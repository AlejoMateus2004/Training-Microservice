package com.training_microservice.domain.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "trainer-summary")
public class TrainerSummary {
    @Id
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private Boolean trainerStatus;
    private Map<Integer, Map<String, Long>> summary;

}
