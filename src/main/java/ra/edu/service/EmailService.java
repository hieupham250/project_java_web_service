package ra.edu.service;

public interface EmailService {
    void sendSimpleMail(String toEmail, String subject, String body);
}
