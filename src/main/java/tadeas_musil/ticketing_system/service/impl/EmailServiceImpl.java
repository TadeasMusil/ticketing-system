package tadeas_musil.ticketing_system.service.impl;

import java.util.Map;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import tadeas_musil.ticketing_system.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendHtmlEmail(String subject, String email, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setRecipients(RecipientType.TO, email);
        message.setSubject(subject);
        message.setContent(text, "text/html");

        javaMailSender.send(message);
    }

    public String createStringFromTemplate(String templatePath, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templatePath, context);
    }

}
