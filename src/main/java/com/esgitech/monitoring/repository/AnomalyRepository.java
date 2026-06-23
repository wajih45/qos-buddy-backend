package com.esgitech.monitoring.repository;

import com.esgitech.monitoring.entity.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
}