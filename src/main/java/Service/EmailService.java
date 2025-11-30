package Service;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {


    private final String username;
    private final String password;

    public EmailService(String username, String password) {
        this.username = username;
        this.password = password;
    }




    public void sendEmail(String to , String subject , String body)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO , InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            //send email
            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        }

        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email",e);
        }
    }

    static void run()
    {

        Dotenv dotenv = Dotenv.load();
        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        EmailService emailService = new EmailService(username , password);

        String subject = "yahya";
        String body = "dear ahmad saif i love you";

        emailService.sendEmail("yahyajaara1411@gmail.com" , subject , body);

    }


    public static void main(String []s)
    {
        run();

    }

}
