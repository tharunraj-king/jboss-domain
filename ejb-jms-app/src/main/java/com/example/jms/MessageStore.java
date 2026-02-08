package com.example.jms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple in-memory store to track received messages for demo purposes
 */
public class MessageStore {
    
    private static final MessageStore INSTANCE = new MessageStore();
    private final List<String> messages = new CopyOnWriteArrayList<>();
    private static final int MAX_MESSAGES = 100;

    private MessageStore() {}

    public static MessageStore getInstance() {
        return INSTANCE;
    }

    public void addMessage(String message) {
        messages.add(0, message); // Add to front
        // Keep only the last MAX_MESSAGES
        while (messages.size() > MAX_MESSAGES) {
            messages.remove(messages.size() - 1);
        }
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public void clear() {
        messages.clear();
    }

    public int getMessageCount() {
        return messages.size();
    }
}
