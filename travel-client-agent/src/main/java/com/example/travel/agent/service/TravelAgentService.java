package com.example.travel.agent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TravelAgentService {
    private final ChatClient chatClient;
    private final String model;

    public TravelAgentService(ChatClient chatClient,
                              @Value("${spring.ai.google.genai.chat.options.model}") String model) {
        this.chatClient = chatClient;
        this.model = model;
    }

    public TravelAgentResult plan(String prompt, String user) {
        long startedAt = System.nanoTime();
        String content = chatClient.prompt()
                .user(userMessage -> userMessage.text("""
                        Passenger/guest name: {user}
                        Travel request: {prompt}
                        Execute all bookings explicitly requested above and then respond in the user's language.
                        """).param("user", user).param("prompt", prompt))
                .call().content();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Gemini returned an empty final response after tool execution");
        }
        return new TravelAgentResult(model, durationMs, content);
    }
}
