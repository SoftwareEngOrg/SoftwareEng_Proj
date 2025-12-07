package Service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setup() {
        emailService = new EmailService("testUser", "testPass");
    }


    @Test
    @DisplayName("sendEmailAsync should return immediately if 'to' is null or empty")
    void testSendEmailAsync_NoRecipient()
    {
        CompletableFuture<Void> result1 = emailService.sendEmailAsync(null, "subj", "body");
        CompletableFuture<Void> result2 = emailService.sendEmailAsync("", "subj", "body");
        assertTrue(result1.isDone());
        assertTrue(result2.isDone());
    }


    @Test
    @DisplayName("sendEmailAsync should call sendEmailInternal with valid recipient (thread-safe)")
    void testSendEmailAsync_ValidRecipient() throws Exception {

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            emailService.sendEmailInternal("a@b.com", "Hello", "Body text");

            transportMock.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
    }


    @Test
    @DisplayName("sendEmailInternal should set correct From, To, Subject, Body")
    void testSendEmailInternal_SetsMessageCorrectly() throws Exception {

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            emailService.sendEmailInternal("a@b.com", "My Subject", "My Body");

            transportMock.verify(() -> Transport.send(argThat(msg -> {
                try {

                    Address[] from = msg.getFrom();
                    if (from.length == 0 || !from[0].toString().equals("testUser")) return false;

                    Address[] to = msg.getRecipients(Message.RecipientType.TO);
                    if (to.length == 0 || !to[0].toString().equals("a@b.com")) return false;

                    if (!msg.getSubject().equals("My Subject")) return false;

                    if (!msg.getContent().toString().equals("My Body")) return false;

                    return true;
                } catch (Exception e) {
                    return false;
                }
            })), times(1));
        }
    }

}
