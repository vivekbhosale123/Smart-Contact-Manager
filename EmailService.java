package org.vdb.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to) {
        boolean f = false;
        
        String To = "vsdb146@gmail.com";
        
        // Gmail SMTP server
        String host = "smtp.gmail.com";
        
        // Set system properties
        Properties properties = System.getProperties();
        
        // Set SMTP server properties
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1: Get session object
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("vsdb146@gmail.com", ""); // Consider storing password securely
            }        
        });
        session.setDebug(true);
        
        try {
            // Step 2: Compose the message
            MimeMessage m = new MimeMessage(session);
            
            // From email
            m.setFrom(new InternetAddress(To));
            
            // Adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            
            // Adding subject to message
            m.setSubject(subject);
            
            // Adding text to message
//            m.setText(message);
            m.setContent(message,"text/html");
            
            // Step 3: Send message using Transport class
            Transport.send(m);
            
            System.out.println("Sent successfully.");
            f = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return f;
    }
}
