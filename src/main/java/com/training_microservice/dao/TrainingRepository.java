package com.training_microservice.dao;

import com.training_microservice.domain.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long>,TrainingRepo {

    @Query("SELECT tr FROM Training tr WHERE tr.traineeUsername = :traineeUsername")
    List<Training> findTrainingByTrainee(@Param("traineeUsername") String traineeUsername);

    @Query("SELECT tr FROM Training tr WHERE tr.trainerUsername = :trainerUsername")
    List<Training> findTrainingByTrainer(@Param("trainerUsername") String trainerUsername);
}