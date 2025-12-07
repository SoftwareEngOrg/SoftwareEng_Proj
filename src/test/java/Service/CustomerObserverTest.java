package Service;

import Domain.MediaItem;
import Domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

class CustomerObserverTest {

    private User user;
    private EmailService emailService;
    private BookServiceCustomer service;
    private CustomerObserver observer;

    @BeforeEach
    void setup() {
        user = mock(User.class);
        emailService = mock(EmailService.class);
        service = mock(BookServiceCustomer.class);
        observer = new CustomerObserver(user, emailService, service);
    }


    @Test
    @DisplayName("Should NOT send email when user email is null")
    void testOnBookAvailable_NoEmailProvided()
    {
        when(user.getEmail()).thenReturn(null);
        when(user.getUsername()).thenReturn("yahya");
        observer.onBookAvailable("12345");
        verify(emailService, never()).sendEmailAsync(anyString(), anyString(), anyString());
        verify(service, never()).findMediaByIsbn(anyString());
    }


    @Test
    @DisplayName("Should NOT send email when user email is empty")
    void testOnBookAvailable_EmptyEmail()
    {
        when(user.getEmail()).thenReturn("");
        when(user.getUsername()).thenReturn("ahmad");
        observer.onBookAvailable("12345");
        verify(emailService, never()).sendEmailAsync(anyString(), anyString(), anyString());
        verify(service, never()).findMediaByIsbn(anyString());
    }


    @Test
    @DisplayName("Should NOT send email when media item not found for ISBN")
    void testOnBookAvailable_ItemNotFound()
    {
        when(user.getEmail()).thenReturn("yahyajaara1411@gmaul.com");
        when(user.getUsername()).thenReturn("yahya");

        when(service.findMediaByIsbn("12345")).thenReturn(null);

        observer.onBookAvailable("12345");

        verify(emailService, never()).sendEmailAsync(anyString(), anyString(), anyString());
    }


    @Test
    @DisplayName("Should send email when user has valid email and media is found")
    void testOnBookAvailable_SuccessfulEmailSend()
    {
        when(user.getEmail()).thenReturn("ahmadseif@gmail.com");
        when(user.getUsername()).thenReturn("ahmad");

        MediaItem item = mock(MediaItem.class);
        when(item.getTitle()).thenReturn("Clean Code");
        when(item.getIsbnOrId()).thenReturn("ID-777");

        when(service.findMediaByIsbn("999")).thenReturn(item);

        observer.onBookAvailable("999");

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService).sendEmailAsync(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());


        assert emailCaptor.getValue().equals("ahmadseif@gmail.com");


        assert subjectCaptor.getValue().equals("Media Item Is Now Available");


        String body = bodyCaptor.getValue();
        assert body.contains("ahmad");
        assert body.contains("Clean Code");
        assert body.contains("ID-777");
    }
}
