package com.ecommerceapp.dubizlelike.utils.email;

import ch.qos.logback.classic.Logger;
import com.ecommerceapp.dubizlelike.dto.EmailRequestDto;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/* Currently, not in use because SendGrid
   registration is not working with me!
*/
@Service("sendGridMailSender")
public class SendGridMailSenderService implements MailSenderStrategy {

    private static final Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(SendGridMailSenderService.class);

    @Value("${sendgrid.sender.email}")
    private String fromEmail;

    private SendGrid sendGrid;

    @Override
    public void sendEmail(EmailRequestDto request) {
        Email from = new Email(fromEmail);
        Email to = new Email(request.getTo());
        String contentType = request.isHtml() ? "text/html" : "text/plain";
        Content content = new Content(contentType, request.getContent());
        Mail mail = new Mail(from, request.getSubject(), to, content);

        Request sendRequest = new Request();
        try {
            sendRequest.setMethod(Method.POST);
            sendRequest.setEndpoint("mail/send"); // an end-point on SendGrid server!
            sendRequest.setEndpoint(mail.build());
            // Store the response object if needed!
            sendGrid.api(sendRequest);

        } catch (IOException ex) {
            logger.info("SendGrid email request failed!");
            logger.error(ex.getMessage());
        }
    }
}
