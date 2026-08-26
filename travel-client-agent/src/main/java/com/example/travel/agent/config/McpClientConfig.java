package com.example.travel.agent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {
    static final String SYSTEM_PROMPT = """
            You are an autonomous travel booking agent serving Vietnamese and English users.
            You have remote tools for flights, hotels, and cabs. Follow these rules:
            1. Search for flights and hotels before booking them.
            2. If the user asks you to book and gives no preference, choose a sensible economical option.
            3. Pass exact IDs returned by search tools into booking tools.
            4. For an airport transfer, use the selected hotel's exact name as drop-off and choose a pickup
               time shortly after the booked flight arrival.
            5. Complete every requested booking autonomously without asking for confirmation when the prompt
               explicitly says to book.
            6. Never claim success unless the corresponding tool returned CONFIRMED or BOOKED.
            7. In the final answer, summarize selections, prices, all booking references, and any partial failure.
            """;

    @Bean
    ChatClient travelChatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder.defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}
