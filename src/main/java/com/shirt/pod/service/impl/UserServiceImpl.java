package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.UserMapper;
import com.shirt.pod.model.dto.request.CreateUserRequest;
import com.shirt.pod.model.dto.request.UpdateUserRequest;
import com.shirt.pod.model.dto.response.UserDTO;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.model.entity.enums.UserStatus;
import com.shirt.pod.repository.RoleRepository;
import com.shirt.pod.repository.UserRepository;
import com.shirt.pod.service.RefreshTokenService;
import com.shirt.pod.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByCreatedDateDesc();
        return userMapper.toDTOList(users);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "id", id));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "email", email));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, normalizedEmail);
        }

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phoneNumber(request.getPhoneNumber())
                .avatarUrl(request.getAvatarUrl())
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .roles(resolveRolesOrDefault(request.getRoleIds()))
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request, Long actorUserId) {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "id", id));
        boolean wasOnlyActiveSuperAdmin = isActiveSuperAdmin(user)
                && userRepository.countUsersByRoleNameAndStatus(RoleConstant.SUPER_ADMIN.name(), UserStatus.ACTIVE) <= 1;

        if (StringUtils.hasText(request.getEmail())) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            if (!user.getEmail().equalsIgnoreCase(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, normalizedEmail);
            }
            user.setEmail(normalizedEmail);
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            refreshTokenService.deleteByUser(user);
        }

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        if (request.getRoleIds() != null) {
            user.setRoles(resolveRolesOrDefault(request.getRoleIds()));
        }

        if (wasOnlyActiveSuperAdmin && !isActiveSuperAdmin(user)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "last active SUPER_ADMIN");
        }

        User savedUser = userRepository.save(user);
        if (!UserStatus.ACTIVE.equals(savedUser.getStatus())) {
            refreshTokenService.deleteByUser(savedUser);
        }

        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long actorUserId) {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "id", id));

        if (id.equals(actorUserId)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "cannot deactivate your own account");
        }

        if (isActiveSuperAdmin(user)
                && userRepository.countUsersByRoleNameAndStatus(RoleConstant.SUPER_ADMIN.name(), UserStatus.ACTIVE) <= 1) {
            throw new AppException(ErrorCode.INVALID_INPUT, "last active SUPER_ADMIN");
        }

        user.setStatus(UserStatus.INACTIVE);
        refreshTokenService.deleteByUser(user);
        userRepository.save(user);
    }

    private Set<Role> resolveRolesOrDefault(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleConstant.USER.name())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "name", RoleConstant.USER.name()));
            return new HashSet<>(Set.of(userRole));
        }

        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "roleIds");
        }
        return roles;
    }

    private boolean isActiveSuperAdmin(User user) {
        return UserStatus.ACTIVE.equals(user.getStatus())
                && user.getRoles().stream()
                .anyMatch(role -> RoleConstant.SUPER_ADMIN.name().equals(role.getName()));
    }
}
