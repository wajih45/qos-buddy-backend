package com.esgitech.monitoring.repository;

import com.esgitech.monitoring.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Long> {
}