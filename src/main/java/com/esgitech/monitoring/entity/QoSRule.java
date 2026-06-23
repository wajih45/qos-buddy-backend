package com.esgitech.monitoring.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "qos_rules")
public class QoSRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metricType;
    private Double threshold;
    private String condition;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public QoSRule() {
    }

    public QoSRule(String metricType, Double threshold, String condition, User user) {
        this.metricType = metricType;
        this.threshold = threshold;
        this.condition = condition;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}