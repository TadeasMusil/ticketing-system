package tadeas_musil.ticketing_system.service;

import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendHtmlEmail(String subject, String email, String message) throws MessagingException;
    
    public String createStringFromTemplate(String templateName, Map<String, Object> variables);
}