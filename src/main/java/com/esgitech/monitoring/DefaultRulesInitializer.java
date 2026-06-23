package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.QoSRule;
import com.esgitech.monitoring.repository.QoSRuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultRulesInitializer implements CommandLineRunner {

    private final QoSRuleRepository qosRuleRepository;

    public DefaultRulesInitializer(QoSRuleRepository qosRuleRepository) {
        this.qosRuleRepository = qosRuleRepository;
    }

    @Override
    public void run(String... args) {
        createRuleIfNotExists("CPU", 80.0, "GREATER_THAN");
        createRuleIfNotExists("RAM", 85.0, "GREATER_THAN");
    }

    private void createRuleIfNotExists(String metricType, Double threshold, String condition) {
        boolean exists = qosRuleRepository.findByMetricTypeIgnoreCase(metricType)
                .stream()
                .anyMatch(rule ->
                        rule.getThreshold().equals(threshold)
                                && rule.getCondition().equalsIgnoreCase(condition)
                );

        if (!exists) {
            QoSRule rule = new QoSRule();
            rule.setMetricType(metricType);
            rule.setThreshold(threshold);
            rule.setCondition(condition);

            qosRuleRepository.save(rule);
        }
    }
}