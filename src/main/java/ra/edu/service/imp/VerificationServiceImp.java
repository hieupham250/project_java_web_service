package ra.edu.service.imp;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import ra.edu.service.VerificationService;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationServiceImp implements VerificationService {
    private final Cache<String, String> verificationCodes = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES) // tự động xóa sau 15 phút
            .maximumSize(1000)
            .build();

    @Override
    public void saveCode(String email, String code) {
        verificationCodes.put(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.getIfPresent(email);
        return savedCode != null && savedCode.equals(code);
    }

    @Override
    public void removeCode(String email) {
        verificationCodes.invalidate(email);
    }
}
