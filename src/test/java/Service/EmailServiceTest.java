package Service;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @Test
    void testSendEmail_SingleAssertion() throws Exception {

        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

            EmailService service = new EmailService("sender@gmail.com", "1234");

            service.sendEmail("yahyajaara1411@gmail.com", "Hello", "Test Body");

            mockedTransport.verify(() -> Transport.send(messageCaptor.capture()), times(1));

            Message msg = messageCaptor.getValue();

            boolean result =
                    msg.getFrom()[0].toString().equals("sender@gmail.com") &&
                            msg.getAllRecipients()[0].toString().equals("user@example.com") &&
                            msg.getSubject().equals("Hello") &&
                            msg.getContent().toString().contains("Test Body");
            assert result;
        }
    }
}
