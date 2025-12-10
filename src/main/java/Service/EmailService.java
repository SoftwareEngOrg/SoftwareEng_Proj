package Service;

import Domain.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
/**
 * The EmailService class provides functionality to send emails asynchronously
 * using a Gmail SMTP server.
 * <p>
 * It allows sending emails by specifying the recipient's email address, subject,
 * and body of the message.
 * </p>
 */
public class EmailService {

    private final String username;
    private final String password;
    /**
     * Constructs an EmailService with the specified username and password.
     * This will be used for authenticating with the Gmail SMTP server.
     *
     * @param username the email username
     * @param password the email password
     */
    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }
    /**
     * Sends an email asynchronously.
     *
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param body the body of the email
     * @return a CompletableFuture that represents the asynchronous operation
     *         and completes once the email is sent
     */
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body) {
        if (to == null || to.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                sendEmailInternal(to, subject, body);
            } catch (Exception e) {

            }
        });
    }
    /**
     * Sends an email synchronously using the specified parameters.
     *
     * @param to the recipient's email address
     * @param subject the subject of the email
     * @param body the body of the email
     * @throws Exception if an error occurs while sending the email
     */
    void sendEmailInternal(String to, String subject, String body) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        // Create a MimeMessage to send the email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        // Send the email
        Transport.send(message);


    }



}
