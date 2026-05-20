package utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EmailUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private EmailUtil() {
    }

    public static boolean sendEmail(String toEmail, String subject, String body) {
        String host = getConfig("MAIL_HOST", "");
        String port = getConfig("MAIL_PORT", "587");
        String username = getConfig("MAIL_USERNAME", "");
        String password = getConfig("MAIL_PASSWORD", "");

        if (host.isBlank() || username.isBlank() || password.isBlank() || toEmail == null || toEmail.isBlank()) {
            logger.warn("Email configuration missing or invalid recipient. Skipping email to {}", toEmail);
            return false;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            logger.error("Error sending email to {}", toEmail, e);
            return false;
        }
    }

    private static String getConfig(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(key);
        }
        return value == null ? defaultValue : value.trim();
    }
}
