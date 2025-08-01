package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.UserLogin;
import ra.edu.dto.request.UserRegister;
import ra.edu.dto.response.JWTResponse;
import ra.edu.entity.Role;
import ra.edu.entity.User;
import ra.edu.enums.RoleName;
import ra.edu.repository.RoleRepository;
import ra.edu.repository.UserRepository;
import ra.edu.security.jwt.JWTProvider;
import ra.edu.security.principal.CustomUserDetails;
import ra.edu.service.AuthService;
import ra.edu.service.EmailService;

import java.time.LocalDate;

@Service
public class AuthServiceImp implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationServiceImp verificationServiceImp;

    @Override
    public User register(UserRegister userRegister) {
        if (userRepository.findByUsername(userRegister.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.findByEmail(userRegister.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (userRepository.findByPhone(userRegister.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

//        if (userRegister.getRole() == null) {
//            throw new IllegalArgumentException("Vai trò không được để trống");
//        }

        User user = User.builder()
                .fullName(userRegister.getFullName())
                .username(userRegister.getUsername())
                .password(passwordEncoder.encode(userRegister.getPassword()))
                .email(userRegister.getEmail())
                .phone(userRegister.getPhone())
                .address(userRegister.getAddress())
                .status(true)
                .role(getRoleFromString("CUSTOMER"))
                .isVerify(false)
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .build();

        userRepository.save(user);

        String code = generateVerificationCode();
        verificationServiceImp.saveCode(user.getEmail(), code);
        emailService.sendSimpleMail(user.getEmail(), "Xác minh Email", "Mã xác thực của bạn là: " + code);

        return user;
    }

    @Override
    public JWTResponse login(UserLogin userLogin) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getUsername(),
                            userLogin.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa");
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Sai tài khoản hoặc mật khẩu");
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Xác thực thất bại");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtProvider.generateToken(customUserDetails.getUsername());

        return JWTResponse.builder()
                .username(customUserDetails.getUsername())
                .email(customUserDetails.getEmail())
                .phone(customUserDetails.getPhone())
                .status(customUserDetails.getStatus())
                .authorities(customUserDetails.getAuthorities())
                .token(token)
                .build();
    }

    private Role getRoleFromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò không được để trống");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Vai trò không hợp lệ: " + roleStr);
        }

        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại trong hệ thống"));
    }

    private String generateVerificationCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public boolean verifyEmail(String email, String code) {
        boolean isValid = verificationServiceImp.verifyCode(email, code);
        if (isValid) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setIsVerify(true);
            userRepository.save(user);
            verificationServiceImp.removeCode(email);
        }
        return isValid;
    }
}
