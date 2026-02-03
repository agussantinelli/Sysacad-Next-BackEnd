package com.sysacad.backend.service;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
}
