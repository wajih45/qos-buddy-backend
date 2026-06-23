package com.esgitech.monitoring.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("QoS Buddy - Password Reset Code");
        message.setText(
                "Hello,\n\n" +
                        "Your password reset code is: " + code + "\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "QoS Buddy Team"
        );

        mailSender.send(message);
    }
}