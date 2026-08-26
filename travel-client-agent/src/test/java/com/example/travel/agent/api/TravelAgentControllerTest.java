package com.example.travel.agent.api;

import com.example.travel.agent.service.TravelAgentService;
import com.example.travel.agent.service.TravelAgentResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TravelAgentController.class)
class TravelAgentControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean TravelAgentService service;

    @Test void returnsAgentResult() throws Exception {
        when(service.plan("Đi Đà Nẵng", "Nguyễn Văn An"))
                .thenReturn(new TravelAgentResult("gemini-3.1-flash-lite", 1234, "Đã đặt xong"));
        mvc.perform(post("/api/travel/plan").contentType("application/json")
                        .content("{\"prompt\":\"Đi Đà Nẵng\",\"user\":\"Nguyễn Văn An\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user").value("Nguyễn Văn An"))
                .andExpect(jsonPath("$.model").value("gemini-3.1-flash-lite"))
                .andExpect(jsonPath("$.durationMs").value(1234))
                .andExpect(jsonPath("$.result").value("Đã đặt xong"));
    }

    @Test void rejectsBlankInput() throws Exception {
        mvc.perform(post("/api/travel/plan").contentType("application/json")
                        .content("{\"prompt\":\"\",\"user\":\"\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.violations.prompt").exists());
    }
}
