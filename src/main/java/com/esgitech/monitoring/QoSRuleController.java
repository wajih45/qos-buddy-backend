package com.esgitech.monitoring;

import com.esgitech.monitoring.entity.QoSRule;
import com.esgitech.monitoring.repository.QoSRuleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qos-rules")
public class QoSRuleController {

    private final QoSRuleRepository qosRuleRepository;

    public QoSRuleController(QoSRuleRepository qosRuleRepository) {
        this.qosRuleRepository = qosRuleRepository;
    }

    @PostMapping
    public QoSRule createRule(@RequestBody QoSRule rule) {
        return qosRuleRepository.save(rule);
    }

    @GetMapping
    public List<QoSRule> getAllRules() {
        return qosRuleRepository.findAll();
    }
}