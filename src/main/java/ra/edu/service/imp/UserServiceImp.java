package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.UserMapper;
import ra.edu.repository.UserRepository;
import ra.edu.service.UserService;

import java.time.LocalDate;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<UserResponse> getUsers(Pageable pageable, String search) {
        Page<User> usersPage;

        if (search == null || search.trim().isEmpty()) {
            usersPage = userRepository.findAllExcludeAdmin(pageable);
        } else {
            usersPage = userRepository.searchByNameOrEmail(search.trim(), pageable);
        }

        return usersPage.map(UserMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse  updateUserByAdmin(Integer id, UserAdminUpdateRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        boolean isEmailChanged = !user.getEmail().equalsIgnoreCase(request.getEmail());
        if (isEmailChanged && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email đã được sử dụng bởi người dùng khác");
        }

        boolean isPhoneChanged = !user.getPhone().equalsIgnoreCase(request.getPhone());
        if (isPhoneChanged && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ConflictException("Số điện thoại đã được sử dụng bởi người dùng khác");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(request.getStatus());
        user.setAvatar(request.getAvatar());
        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateStatus(Integer id, Boolean status) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        user.setStatus(status);
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse softDelete(Integer id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);

        return UserMapper.toResponse(user);
    }
}
