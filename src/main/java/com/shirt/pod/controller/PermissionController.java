package com.shirt.pod.controller;

import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.PermissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.shirt.pod.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.shirt.pod.security.SecurityConstants.PERMISSION_VIEW;


@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission", description = "APIs for managing permissions in the system")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Returns a list of all permissions in the system. Requires PERMISSION_VIEW authority")
    @PreAuthorize("hasAuthority('" + PERMISSION_VIEW + "')")
    public ApiResponse<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ApiResponse.<List<PermissionDTO>>builder()
                .code(200)
                .message("get all permissions successfully")
                .data(permissions)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Returns details of a specific permission by ID. Requires PERMISSION_VIEW authority")
    @PreAuthorize("hasAuthority('" + PERMISSION_VIEW + "')")
    public ApiResponse<PermissionDTO> getPermissionById(@PathVariable Long id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ApiResponse.<PermissionDTO>builder()
                .code(200)
                .message("get permission by id successfully")
                .data(permission)
                .build();
    }
}
