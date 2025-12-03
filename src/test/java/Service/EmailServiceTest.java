package Service;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void testSendEmailInternal_withMock() throws Exception {
        EmailService emailService = new EmailService("test@gmail.com", "password");

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            transportMock.when(() -> Transport.send(any(Message.class))).thenAnswer(invocation -> null);


            emailService.sendEmailInternal("to@test.com", "Subject", "Body");


            transportMock.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }
}
