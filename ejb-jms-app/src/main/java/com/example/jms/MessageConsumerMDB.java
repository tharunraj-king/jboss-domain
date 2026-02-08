package com.example.jms;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean that consumes messages from the queue
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/DemoQueue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class MessageConsumerMDB implements MessageListener {
    
    private static final Logger LOGGER = Logger.getLogger(MessageConsumerMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                String sender = textMessage.getStringProperty("sender");
                String sourceHost = textMessage.getStringProperty("host");
                
                String processingHost = System.getProperty("jboss.host.name", "unknown");
                
                LOGGER.info("===========================================");
                LOGGER.info("MDB received message on host: " + processingHost);
                LOGGER.info("Message content: " + text);
                LOGGER.info("Sender: " + sender);
                LOGGER.info("Source host: " + sourceHost);
                LOGGER.info("===========================================");
                
                // Store the message for retrieval via the servlet
                MessageStore.getInstance().addMessage(
                    String.format("[%s] Received: '%s' (from %s, processed on %s)", 
                        java.time.LocalDateTime.now(), text, sourceHost, processingHost)
                );
            } else {
                LOGGER.warning("Received non-text message: " + message.getClass().getName());
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing message", e);
        }
    }
}
