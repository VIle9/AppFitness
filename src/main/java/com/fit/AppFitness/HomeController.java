package com.fit.AppFitness;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home(){
        Map<String, Object> response = new HashMap<>();
        response.put("name", "AppFitness API");
        response.put("version", "1.0.0");
        response.put("status", "✅ Running");
        response.put("documentation", "http://localhost:8080/swagger-ui.html");

        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("Auth", "/api/v1/auth");
        endpoints.put("Users", "/api/v1/users");
        endpoints.put("Foods", "/api/v1/foods");
        endpoints.put("Meals", "/api/v1/meals");

        endpoints.put("endpoints", endpoints);

        return response;
    }
}
