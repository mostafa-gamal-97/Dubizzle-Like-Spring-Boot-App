package com.ecommerceapp.dubizlelike.utils.email;

import ch.qos.logback.classic.Logger;
import com.ecommerceapp.dubizlelike.dto.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("smtpMailSender")
@Primary
@RequiredArgsConstructor
public class SmtpMailSenderService implements MailSenderStrategy {

    private static final Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(SmtpMailSenderService.class);

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailRequestDto request) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(request.getTo());
        message.setText(request.getContent());
        message.setSubject(request.getSubject());
        mailSender.send(message);

        logger.info("Mail sent successfully through gmail smtp server!");
    }
}
