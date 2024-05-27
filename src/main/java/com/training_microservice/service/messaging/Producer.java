package com.training_microservice.service.messaging;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Producer {

    private JmsTemplate jmsTemplate;

    public void sendMessage(String destination, Object messageContent, String processId){
        jmsTemplate.convertAndSend(destination, messageContent, message -> {
            message.setStringProperty("processId", processId);
            return message;
        });
    }
}
