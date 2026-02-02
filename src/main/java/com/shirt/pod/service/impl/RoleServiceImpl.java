package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.RoleMapper;
import com.shirt.pod.model.dto.request.CreateRoleRequest;
import com.shirt.pod.model.dto.request.UpdateRoleRequest;
import com.shirt.pod.model.dto.response.RoleDTO;
import com.shirt.pod.model.entity.Permission;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.repository.PermissionRepository;
import com.shirt.pod.repository.RoleRepository;
import com.shirt.pod.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "id", id));
        return roleMapper.toDTO(role);
    }

    @Override
    @Transactional
    public RoleDTO createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.DUPLICATE_NAME, request.getName());
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(new HashSet<>())
                .build();

        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(
                    permissionRepository.findAllById(request.getPermissionIds()));

            if (permissions.size() != request.getPermissionIds().size()) {
                throw new AppException(ErrorCode.INVALID_INPUT, "permissionIds");
            }

            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleMapper.toDTO(savedRole);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "id", id));

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!role.getName().equals(request.getName()) &&
                    roleRepository.existsByName(request.getName())) {
                throw new AppException(ErrorCode.DUPLICATE_NAME, request.getName());
            }
            role.setName(request.getName());
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        if (request.getPermissionIds() != null) {
            if (request.getPermissionIds().isEmpty()) {
                role.getPermissions().clear();
            } else {
                Set<Permission> permissions = new HashSet<>(
                        permissionRepository.findAllById(request.getPermissionIds()));

                if (permissions.size() != request.getPermissionIds().size()) {
                    throw new AppException(ErrorCode.INVALID_INPUT, "permissionIds");
                }

                role.setPermissions(permissions);
            }
        }

        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDTO(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "id", id));

        if (RoleConstant.SUPER_ADMIN.name().equals(role.getName())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_DEFAULT_ROLE, role.getName());
        }

        roleRepository.delete(role);
    }
}
