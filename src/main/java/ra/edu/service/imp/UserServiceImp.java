package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.ChangePasswordRequest;
import ra.edu.dto.request.UserProfileUpdateRequest;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.UserMapper;
import ra.edu.repository.UserRepository;
import ra.edu.service.UserService;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return UserMapper.toResponse(user);
    }

    @Override
    public void updateProfile(String username, UserProfileUpdateRequest request) {
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
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
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
    }
}
