package com.example.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EJB that produces JMS messages to a queue
 */
@Stateless
public class MessageProducer {
    
    private static final Logger LOGGER = Logger.getLogger(MessageProducer.class.getName());

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/DemoQueue")
    private Queue queue;

    public void sendMessage(String messageText) {
        LOGGER.info("Sending message to queue: " + messageText);
        
        try (JMSContext context = connectionFactory.createContext()) {
            JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(messageText);
            message.setStringProperty("sender", "MessageProducer");
            message.setStringProperty("host", System.getProperty("jboss.host.name", "unknown"));
            producer.send(queue, message);
            LOGGER.info("Message sent successfully!");
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error sending message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public void sendMultipleMessages(int count) {
        LOGGER.info("Sending " + count + " messages to queue");
        
        try (JMSContext context = connectionFactory.createContext()) {
            JMSProducer producer = context.createProducer();
            for (int i = 1; i <= count; i++) {
                TextMessage message = context.createTextMessage("Message #" + i + " from " + System.getProperty("jboss.host.name", "unknown"));
                message.setIntProperty("messageNumber", i);
                producer.send(queue, message);
            }
            LOGGER.info("All " + count + " messages sent successfully!");
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error sending messages", e);
            throw new RuntimeException("Failed to send messages", e);
        }
    }
}
