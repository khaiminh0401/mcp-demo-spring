package com.example.travel.cab.config;

import com.example.travel.cab.tool.CabTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CabToolConfig {
    @Bean ToolCallbackProvider cabToolCallbackProvider(CabTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
