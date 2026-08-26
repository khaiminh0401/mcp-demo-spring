package com.example.travel.agent.api;

import com.example.travel.agent.service.TravelAgentService;
import com.example.travel.agent.service.TravelAgentResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel")
public class TravelAgentController {
    private final TravelAgentService service;

    public TravelAgentController(TravelAgentService service) { this.service = service; }

    @PostMapping("/plan")
    public ResponseEntity<TravelPlanResponse> plan(@Valid @RequestBody TravelPlanRequest request) {
        TravelAgentResult result = service.plan(request.getPrompt(), request.getUser());
        return ResponseEntity.ok(TravelPlanResponse.builder()
                .user(request.getUser()).model(result.model()).durationMs(result.durationMs())
                .result(result.content()).build());
    }
}
