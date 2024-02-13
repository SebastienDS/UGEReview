package fr.uge.revue.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Objects;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = Objects.requireNonNull(mailSender);
    }

    @Async
    public void send(String to, String subject, String content) {
        Objects.requireNonNull(to);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(content);
        var email = new SimpleMailMessage();
        email.setTo(to);
        email.setFrom("UGERevue Support");
        email.setSubject(subject);
        email.setText(content);

        System.err.println(email);
//        mailSender.send(email);
        // TODO: set real mail host
    }
}
