package ra.edu.service;

public interface VerificationService {
    void saveCode(String email, String code);
    boolean verifyCode(String email, String code);
    void removeCode(String email);
}
