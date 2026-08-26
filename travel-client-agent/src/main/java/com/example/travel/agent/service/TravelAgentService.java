package com.example.travel.agent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TravelAgentService {
    private final ChatClient chatClient;

    public TravelAgentService(ChatClient chatClient) { this.chatClient = chatClient; }

    public String plan(String prompt, String user) {
        return chatClient.prompt()
                .user(userMessage -> userMessage.text("""
                        Passenger/guest name: {user}
                        Travel request: {prompt}
                        Execute all bookings explicitly requested above and then respond in the user's language.
                        """).param("user", user).param("prompt", prompt))
                .call().content();
    }
}
