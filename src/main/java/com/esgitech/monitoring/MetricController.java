package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.Metric;
import com.esgitech.monitoring.service.MonitoringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metrics")
public class MetricController {

    private final MonitoringService monitoringService;

    public MetricController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @PostMapping
    public Metric createMetric(@RequestBody Metric metric) {
        return monitoringService.createMetric(metric);
    }

    @GetMapping
    public List<Metric> getAllMetrics() {
        return monitoringService.getAllMetrics();
    }
}