package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreateUserRequest;
import com.shirt.pod.model.dto.request.UpdateUserRequest;
import com.shirt.pod.model.dto.response.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    UserDTO createUser(CreateUserRequest request);

    UserDTO updateUser(Long id, UpdateUserRequest request, Long actorUserId);

    void deleteUser(Long id, Long actorUserId);
}
