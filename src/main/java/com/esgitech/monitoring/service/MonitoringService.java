package com.esgitech.monitoring.service;

import com.esgitech.monitoring.entity.Alert;
import com.esgitech.monitoring.entity.Anomaly;
import com.esgitech.monitoring.entity.Metric;
import com.esgitech.monitoring.entity.QoSRule;
import com.esgitech.monitoring.repository.AlertRepository;
import com.esgitech.monitoring.repository.AnomalyRepository;
import com.esgitech.monitoring.repository.MetricRepository;
import com.esgitech.monitoring.repository.QoSRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitoringService {

    private final MetricRepository metricRepository;
    private final AlertRepository alertRepository;
    private final AnomalyRepository anomalyRepository;
    private final QoSRuleRepository qosRuleRepository;

    // URL du microservice IA Flask (Isolation Forest)
    private static final String AI_SERVICE_URL = "http://localhost:5001/predict";

    public MonitoringService(MetricRepository metricRepository,
                             AlertRepository alertRepository,
                             AnomalyRepository anomalyRepository,
                             QoSRuleRepository qosRuleRepository) {
        this.metricRepository = metricRepository;
        this.alertRepository = alertRepository;
        this.anomalyRepository = anomalyRepository;
        this.qosRuleRepository = qosRuleRepository;
    }

    public Metric createMetric(Metric metric) {

        if (metric.getTimestamp() == null) {
            metric.setTimestamp(LocalDateTime.now());
        }

        Metric savedMetric = metricRepository.save(metric);

        // ---------------------------------------------------------------
        // 1. DÉTECTION PAR SEUIL (règles QoS) — méthode existante
        // ---------------------------------------------------------------
        List<QoSRule> rules =
                qosRuleRepository.findByMetricTypeIgnoreCase(savedMetric.getType());

        for (QoSRule rule : rules) {

            if (savedMetric.getValue() == null
                    || rule.getThreshold() == null
                    || rule.getCondition() == null) {
                continue;
            }

            boolean anomalyDetected = false;

            switch (rule.getCondition().toUpperCase()) {
                case "GREATER_THAN":
                    anomalyDetected = savedMetric.getValue() > rule.getThreshold();
                    break;

                case "LESS_THAN":
                    anomalyDetected = savedMetric.getValue() < rule.getThreshold();
                    break;

                case "EQUAL":
                    anomalyDetected = savedMetric.getValue().equals(rule.getThreshold());
                    break;

                default:
                    break;
            }

            if (anomalyDetected) {

                String description = savedMetric.getType()
                        + " value "
                        + savedMetric.getValue()
                        + " exceeded threshold "
                        + rule.getThreshold();

                String severity = savedMetric.getValue() > 90 ? "HIGH" : "MEDIUM";

                createAnomalyAndAlert(description, severity);
            }
        }

        // ---------------------------------------------------------------
        // 2. DÉTECTION PAR IA (Isolation Forest via Flask) — nouvelle méthode
        // ---------------------------------------------------------------
        if (savedMetric.getValue() != null) {
            boolean aiAnomaly = checkAnomalyWithAI(savedMetric.getValue());

            if (aiAnomaly) {
                String description = "Anomalie détectée par IA (Isolation Forest) sur "
                        + savedMetric.getType()
                        + " = "
                        + savedMetric.getValue();

                createAnomalyAndAlert(description, "HIGH");
            }
        }

        return savedMetric;
    }

    // ------------------------------------------------------------------
    // Appel au microservice IA Flask
    // ------------------------------------------------------------------
    private boolean checkAnomalyWithAI(Double value) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("value", value);

            Map<String, Object> response =
                    restTemplate.postForObject(AI_SERVICE_URL, requestBody, Map.class);

            if (response != null && response.get("anomaly") != null) {
                return (Boolean) response.get("anomaly");
            }
        } catch (Exception e) {
            // Si le service IA est indisponible, on ne bloque pas le système
            System.out.println("Service IA indisponible : " + e.getMessage());
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Méthode utilitaire : créer une anomalie + une alerte
    // ------------------------------------------------------------------
    private void createAnomalyAndAlert(String description, String severity) {

        Anomaly anomaly = new Anomaly();
        anomaly.setDate(LocalDateTime.now());
        anomaly.setSeverity(severity);
        anomaly.setDescription(description);

        Anomaly savedAnomaly = anomalyRepository.save(anomaly);

        Alert alert = new Alert();
        alert.setMessage(description);
        alert.setStatus("ACTIVE");
        alert.setDate(LocalDateTime.now());
        alert.setAnomaly(savedAnomaly);

        alertRepository.save(alert);
    }

    public List<Metric> getAllMetrics() {
        return metricRepository.findAll();
    }
}