package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateUserRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.shirt.pod.model.dto.response.UserDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.shirt.pod.security.SecurityConstants.USER_CREATE;
import static com.shirt.pod.security.SecurityConstants.USER_DELETE;
import static com.shirt.pod.security.SecurityConstants.USER_VIEW;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "APIs for user management")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system. Only accessible by ADMIN role")
    @PreAuthorize("hasAuthority('" + USER_VIEW + "')")
    public ApiResponse<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();

        return ApiResponse.<List<UserDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Get all users successfully")
                .data(users)
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns detailed information of the currently authenticated user")
    public ApiResponse<UserDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDTO user = userService.getUserById(userDetails.getId());

        return ApiResponse.<UserDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Get current user successfully")
                .data(user)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Returns details of a specific user by ID. Only accessible by ADMIN role")
    @PreAuthorize("hasAuthority('" + USER_VIEW + "')")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);

        return ApiResponse.<UserDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Get user successfully")
                .data(user)
                .build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Returns details of a specific user by email address")
    @PreAuthorize("hasAuthority('" + USER_VIEW + "')")
    public ApiResponse<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);

        return ApiResponse.<UserDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Get user successfully")
                .data(user)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user account with basic information")
    @PreAuthorize("hasAuthority('" + USER_CREATE + "')")
    public ApiResponse<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO user = userService.createUser(request);

        return ApiResponse.<UserDTO>builder()
                .code(HttpStatus.OK.value())
                .message("User created successfully")
                .data(user)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user from the system by ID. Only accessible by ADMIN role")
    @PreAuthorize("hasAuthority('" + USER_DELETE + "')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("User deleted successfully")
                .build();
    }
}
