package ra.edu.service.imp;

import org.springframework.stereotype.Service;
import ra.edu.service.VerificationService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationServiceImp implements VerificationService {
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @Override
    public void saveCode(String email, String code) {
        verificationCodes.put(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        return savedCode != null && savedCode.equals(code);
    }

    @Override
    public void removeCode(String email) {
        verificationCodes.remove(email);
    }
}
