package com.esgitech.monitoring.controller;
import com.esgitech.monitoring.repository.AnomalyRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AnomalyRepository anomalyRepository;

    public AIController(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> stats = new HashMap<>();

        long totalAnomalies = anomalyRepository.count();
        long highAnomalies = anomalyRepository.countBySeverity("HIGH");

        stats.put("totalAnomalies", totalAnomalies);
        stats.put("highAnomalies", highAnomalies);
        stats.put("model", "Isolation Forest");

        return stats;
    }
}