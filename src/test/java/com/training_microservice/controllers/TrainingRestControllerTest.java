package com.training_microservice.controllers;

import com.training_microservice.domain.records.TrainingRecord;
import com.training_microservice.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class TrainingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    private String requestBody;
    @BeforeEach
    void setUp() {
       requestBody = "{\n" +
        "  \"traineeUsername\": \"traineeUsername\",\n" +
        "  \"trainerUsername\": \"trainerUsername\",\n" +
        "  \"trainingName\": \"TrainingName\",\n" +
        "  \"trainingDate\": \"" + LocalDate.now() + "\",\n" +
        "  \"trainingDuration\": 60\n" +
        "}";
    }

    @Test
    public void testSaveTrainingSuccess() throws Exception {

        when(trainingService.saveTraining(any(TrainingRecord.TrainingRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/training")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(trainingService, times(1)).saveTraining(any(TrainingRecord.TrainingRequest.class));
    }

    @Test
    public void testSaveTrainingBadRequest() throws Exception {
        when(trainingService.saveTraining(any(TrainingRecord.TrainingRequest.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(trainingService, times(1)).saveTraining(any(TrainingRecord.TrainingRequest.class));

    }

//    @Test
//    public void testUpdateTrainingStatusSuccess() throws Exception {
//
//        when(trainingService.updateTrainingStatusToCompleted(1L))
//                .thenReturn(ResponseEntity.ok().build());
//
//        mockMvc
//                .perform(put("/training/status/{trainingId}", 1L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(trainingService, times(1)).updateTrainingStatusToCompleted(1L);
//    }
//
//    @Test
//    public void testUpdateTrainingStatusBadRequest() throws Exception {
//
//        when(trainingService.updateTrainingStatusToCompleted(1L))
//                .thenReturn(ResponseEntity.badRequest().build());
//
//        mockMvc
//                .perform(put("/training/status/{trainingId}", 1L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        verify(trainingService, times(0)).updateTrainingStatusToCompleted(1L);
//
//    }
}