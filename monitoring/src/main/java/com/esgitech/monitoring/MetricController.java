package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.Alert;
import com.esgitech.monitoring.entity.Metric;
import com.esgitech.monitoring.repository.AlertRepository;
import com.esgitech.monitoring.repository.MetricRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/metrics")
public class MetricController {

    private final MetricRepository metricRepository;
    private final AlertRepository alertRepository;

    public MetricController(MetricRepository metricRepository, AlertRepository alertRepository) {
        this.metricRepository = metricRepository;
        this.alertRepository = alertRepository;
    }

    @PostMapping
    public Metric createMetric(@RequestBody Metric metric) {
        Metric savedMetric = metricRepository.save(metric);

        if ("CPU".equalsIgnoreCase(metric.getType()) && metric.getValue() > 80) {
            Alert alert = new Alert();
            alert.setMessage("High CPU usage");
            alert.setStatus("ACTIVE");
            alert.setDate(LocalDateTime.now());

            alertRepository.save(alert);
        }

        return savedMetric;
    }

    @GetMapping
    public List<Metric> getAllMetrics() {
        return metricRepository.findAll();
    }
}