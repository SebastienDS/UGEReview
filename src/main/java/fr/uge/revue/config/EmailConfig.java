package fr.uge.revue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {
    @Bean
    public JavaMailSenderImpl getMailSender() {
        var sender = new JavaMailSenderImpl();
        sender.setHost("uge.revue.com");
        return sender;
    }
}
