package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.Alert;
import com.esgitech.monitoring.repository.AlertRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertRepository.save(alert);
    }

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
}