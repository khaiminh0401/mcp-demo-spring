package com.example.travel.flight.config;

import com.example.travel.flight.tool.FlightTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlightToolConfig {
    @Bean
    ToolCallbackProvider flightToolCallbackProvider(FlightTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
