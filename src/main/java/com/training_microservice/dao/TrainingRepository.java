package com.training_microservice.dao;

import com.training_microservice.domain.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
public interface TrainingRepository extends JpaRepository<Training, Long>,TrainingRepo {

    @Query("SELECT tr FROM Training tr WHERE tr.trainerUsername = :trainerUsername")
    List<Training> findTrainingByTrainer(@Param("trainerUsername") String trainerUsername);


    @Query("SELECT DISTINCT tr FROM Training tr " +
            "WHERE tr.trainerUsername = :trainerUsername " +
            "AND (:periodFrom IS NULL OR tr.trainingDate >= :periodFrom) " +
            "AND (:periodTo IS NULL OR tr.trainingDate <= :periodTo) " +
            "AND (COALESCE(:traineeUsername, '') = '' OR LOWER(tr.traineeUsername) LIKE LOWER(:traineeUsername)) ")
    List<Training> findTrainingByTrainerUsernameAndTrainingParams(
            @Param("trainerUsername") String trainerUsername,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("traineeUsername") String traineeUsername
    );

    @Query("SELECT DISTINCT tr FROM Training tr " +
            "WHERE tr.traineeUsername = :traineeUsername " +
            "AND (:periodFrom IS NULL OR tr.trainingDate >= :periodFrom) " +
            "AND (:periodTo IS NULL OR tr.trainingDate <= :periodTo) " +
            "AND (COALESCE(:trainerUsername, '') = '' OR LOWER(tr.trainerUsername) LIKE LOWER(:trainerUsername)) ")
    List<Training> findTrainingByTraineeUsernameAndTrainingParams(
            @Param("traineeUsername") String traineeUsername,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("trainerUsername") String trainerUsername
    );

}