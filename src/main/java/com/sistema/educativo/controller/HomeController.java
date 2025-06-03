package com.sistema.educativo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "API Sistema Educativo funcionando correctamente");
        response.put("version", "1.0.0");
        response.put("endpoints", "/api/auth/login");
        return response;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("mensaje", "Servicio funcionando");
        return response;
    }
}