package com.esgitech.monitoring.repository;

import com.esgitech.monitoring.entity.QoSRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QoSRuleRepository extends JpaRepository<QoSRule, Long> {
    List<QoSRule> findByMetricTypeIgnoreCase(String metricType);
}