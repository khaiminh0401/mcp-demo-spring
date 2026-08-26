package com.example.travel.agent.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TravelPlanRequest {
    @NotBlank(message = "prompt must not be blank")
    private String prompt;
    @NotBlank(message = "user must not be blank")
    private String user;
}
