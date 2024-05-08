package com.training_microservice.domain.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Training")
public class Training implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Long Id;

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
}
