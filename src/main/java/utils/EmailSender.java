package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.io.File;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class EmailSender {

    private static Session setupSession() {
        // Replace with your email credentials and SMTP server details
        final String fromEmail = "demoduanjava@gmail.com"; // GMAIL
        final String password = "dnayjtrgkmprjfdq"; // GMAIL App password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        props.put("mail.smtp.port", "587"); // TLS Port
        props.put("mail.smtp.auth", "true"); // enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS

        // Create Authenticator
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        return Session.getInstance(props, auth);
    }

    public static void sendEmail(String toEmail, String subject, String body) {
        Session session = setupSession();

        try {
            MimeMessage msg = new MimeMessage(session);
            // set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("your-email@example.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse("your-email@example.com", false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            
            Transport.send(msg);

            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public static void sendEmail(String toEmail, String subject, String body, String filePath) {
        Session session = setupSession();

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("your-email@example.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse("your-email@example.com", false));
            msg.setSubject(subject, "UTF-8");
            
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            if (filePath != null) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(new File(filePath).getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Send the complete message parts
            msg.setContent(multipart);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            
            Transport.send(msg);

            System.out.println("Email with attachment sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }
}
