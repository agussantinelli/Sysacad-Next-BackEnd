package com.sysacad.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock private JavaMailSender emailSender;
    @Mock private TemplateEngine templateEngine;
    @Mock private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "admin@sysacad.com");
    }

    @Test
    void sendEmail_DeberiaLlamarASender() {
        emailService.sendEmail("to@test.com", "Subject", "Body");

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtmlEmail_DeberiaProcesarTemplateYEnviar() {
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html></html>");

        Map<String, Object> variables = new HashMap<>();
        variables.put("user", "Juan");

        emailService.sendHtmlEmail("to@test.com", "Subject", "template", variables);

        verify(templateEngine).process(eq("template"), any(Context.class));
        verify(emailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_DeberiaCapturarExcepcionYNoRethrow() {
        doThrow(new RuntimeException("Mail server down")).when(emailSender).send(any(SimpleMailMessage.class));

        // No debería fallar el test
        emailService.sendEmail("to@test.com", "Subject", "Body");

        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}
