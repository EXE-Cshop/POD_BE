package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateRoleRequest;
import com.shirt.pod.model.dto.request.UpdateRoleRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.shirt.pod.model.dto.response.RoleDTO;
import com.shirt.pod.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.shirt.pod.security.SecurityConstants.ROLE_CREATE;
import static com.shirt.pod.security.SecurityConstants.ROLE_DELETE;
import static com.shirt.pod.security.SecurityConstants.ROLE_UPDATE;
import static com.shirt.pod.security.SecurityConstants.ROLE_VIEW;


@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "APIs for managing roles and permissions")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles", description = "Returns a list of all roles in the system. Requires ROLE_VIEW authority")
    @PreAuthorize("hasAuthority('" + ROLE_VIEW + "')")
    public ApiResponse<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ApiResponse.<List<RoleDTO>>builder()
                .code(200)
                .message("get all roles successfully")
                .data(roles)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Returns details of a specific role by ID. Requires ROLE_VIEW authority")
    @PreAuthorize("hasAuthority('" + ROLE_VIEW + "')")
    public ApiResponse<RoleDTO> getRoleById(@PathVariable Long id) {
        RoleDTO role = roleService.getRoleById(id);
        return ApiResponse.<RoleDTO>builder()
                .code(200)
                .message("get role by id successfully")
                .data(role)
                .build();
    }

    @PostMapping
    @Operation(summary = "Create new role", description = "Create a new role with name and list of permissions. Requires ROLE_CREATE authority")
    @PreAuthorize("hasAuthority('" + ROLE_CREATE + "')")
    public ApiResponse<RoleDTO> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDTO role = roleService.createRole(request);
        return ApiResponse.<RoleDTO>builder()
                .code(200)
                .message("create role successfully")
                .data(role)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Update role information including name and list of permissions. Requires ROLE_UPDATE authority")
    @PreAuthorize("hasAuthority('" + ROLE_UPDATE + "')")
    public ApiResponse<RoleDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        RoleDTO role = roleService.updateRole(id, request);
        return ApiResponse.<RoleDTO>builder()
                .code(200)
                .message("update role successfully")
                .data(role)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Delete a role from the system by ID. Requires ROLE_DELETE authority")
    @PreAuthorize("hasAuthority('" + ROLE_DELETE + "')")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("delete role successfully")
                .build();
    }
}
