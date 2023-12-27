package org.mourya.sfgjms.sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import org.mourya.sfgjms.config.JmsConfig;
import org.mourya.sfgjms.model.HelloWorldMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {


        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello World!")
                .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);


    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {


        HelloWorldMessage message = HelloWorldMessage
                .builder()
                .id(UUID.randomUUID())
                .message("Hello")
                .build();

        /*Message receiveMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_REC_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try{
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "org.mourya.sfgjms.model.HelloWorldMessage");

                    System.out.println("Sending Hello.");
                } catch (JsonProcessingException e) {
                    throw new JMSException("BOOM");
                }
                return helloMessage;
            }
        });*/

        Message receiveMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_REC_QUEUE, session -> {
                Message helloMessage = null;
                try{
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "org.mourya.sfgjms.model.HelloWorldMessage");

                    System.out.println("Sending Hello.");
                } catch (JsonProcessingException e) {
                    throw new JMSException("BOOM");
                }
                return helloMessage;
        });

        System.out.println(receiveMsg.getBody(String.class));

    }

}