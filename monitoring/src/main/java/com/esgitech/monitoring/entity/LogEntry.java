package com.esgitech.monitoring.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime timestamp;

    private String serviceName;

    public LogEntry() {
    }

    public LogEntry(String level, String message, LocalDateTime timestamp, String serviceName) {
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.serviceName = serviceName;
    }

    public Long getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}