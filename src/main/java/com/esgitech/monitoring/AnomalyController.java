package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.Anomaly;
import com.esgitech.monitoring.repository.AnomalyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/anomalies")
public class AnomalyController {

    private final AnomalyRepository anomalyRepository;

    public AnomalyController(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    @PostMapping
    public Anomaly createAnomaly(@RequestBody Anomaly anomaly) {
        return anomalyRepository.save(anomaly);
    }

    @GetMapping
    public List<Anomaly> getAllAnomalies() {
        return anomalyRepository.findAll();
    }
}