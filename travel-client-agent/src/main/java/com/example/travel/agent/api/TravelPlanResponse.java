package com.example.travel.agent.api;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TravelPlanResponse {
    String user;
    String model;
    long durationMs;
    String result;
}
