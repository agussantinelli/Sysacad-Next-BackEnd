package com.sysacad.backend.service;

import java.util.Map;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables);
}
