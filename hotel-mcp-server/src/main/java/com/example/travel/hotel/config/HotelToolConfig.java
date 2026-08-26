package com.example.travel.hotel.config;

import com.example.travel.hotel.tool.HotelTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HotelToolConfig {
    @Bean ToolCallbackProvider hotelToolCallbackProvider(HotelTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
