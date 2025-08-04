package ra.edu.service.imp;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.ChangePasswordRequest;
import ra.edu.dto.request.UserLogin;
import ra.edu.dto.request.UserProfileUpdateRequest;
import ra.edu.dto.request.UserRegister;
import ra.edu.dto.response.JWTResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.Role;
import ra.edu.entity.User;
import ra.edu.enums.RoleName;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.UserMapper;
import ra.edu.repository.RoleRepository;
import ra.edu.repository.UserRepository;
import ra.edu.security.jwt.JWTProvider;
import ra.edu.security.principal.CustomUserDetails;
import ra.edu.service.AuthService;
import ra.edu.service.EmailService;
import ra.edu.service.VerificationService;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

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
    private VerificationService verificationService;

    private final Cache<String, UserRegister> tempUsers = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES) // Tự động xóa sau 15 phút
            .maximumSize(1000) // Tối đa 1000 phần tử
            .build();

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

        // Lưu bản copy có password đã mã hóa
        UserRegister temp = new UserRegister();
        temp.setFullName(userRegister.getFullName());
        temp.setUsername(userRegister.getUsername());
        temp.setPassword(passwordEncoder.encode(userRegister.getPassword()));
        temp.setEmail(userRegister.getEmail());
        temp.setPhone(userRegister.getPhone());
        temp.setAddress(userRegister.getAddress());

        tempUsers.put(userRegister.getEmail(), temp);

        String code = generateVerificationCode();
        verificationService.saveCode(userRegister.getEmail(), code);
        emailService.sendSimpleMail(userRegister.getEmail(), "Xác minh Email", "Mã xác thực của bạn là: " + code);

        return null;
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

    @Override
    public boolean verifyEmail(String email, String code) {
        boolean isValid = verificationService.verifyCode(email, code);
        if (isValid) {
            UserRegister temp = tempUsers.getIfPresent(email);
            if (temp == null) {
                throw new IllegalArgumentException("Không tìm thấy thông tin đăng ký tạm thời");
            }

            User user = User.builder()
                    .fullName(temp.getFullName())
                    .username(temp.getUsername())
                    .password(temp.getPassword())
                    .email(temp.getEmail())
                    .phone(temp.getPhone())
                    .address(temp.getAddress())
                    .status(true)
                    .isVerify(true)
                    .isDeleted(false)
                    .role(getRoleFromString("CUSTOMER"))
                    .createdAt(LocalDate.now())
                    .build();

            userRepository.save(user);

            tempUsers.invalidate(email);
            verificationService.removeCode(email);
        }
        return isValid;
    }

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateProfile(String username, UserProfileUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        boolean isPhoneChanged = !user.getPhone().equalsIgnoreCase(request.getPhone());
        if (isPhoneChanged && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ConflictException("Số điện thoại đã được sử dụng bởi người dùng khác");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setAvatar(request.getAvatar());

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ConflictException("Mật khẩu cũ không đúng");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ConflictException("Mật khẩu xác nhận không khớp");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return UserMapper.toResponse(user);
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
}
