package com.training_microservice.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Training")
public class Training implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Column(name = "traineeUsername", nullable = false)
    private String traineeUsername;

    @Column(name = "trainerUsername", nullable = false)
    private String trainerUsername;

    @Column(name = "trainingName", nullable = false)
    private String trainingName;

    @Column(name = "trainingTypeId")
    private Long trainingTypeId;

    @Column(name = "trainingDate", nullable = false)
    private LocalDate trainingDate;

    @Column(name = "trainingDuration", nullable = false)
    private Long trainingDuration;

    @Column(name = "trainingIsCompleted", nullable = false)
    private Boolean trainingIsCompleted;

    @PrePersist
    protected void onCreate() {
        if (this.trainingIsCompleted == null) {
            this.trainingIsCompleted = false;
        }
    }

    public Training() {
    }

    public Training(String traineeUsername, String trainerUsername, String trainingName, Long trainingTypeId, LocalDate trainingDate, Long trainingDuration) {
        this.traineeUsername = traineeUsername;
        this.trainerUsername = trainerUsername;
        this.trainingName = trainingName;
        this.trainingTypeId = trainingTypeId;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
