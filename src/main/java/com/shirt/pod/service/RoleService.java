package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreateRoleRequest;
import com.shirt.pod.model.dto.request.UpdateRoleRequest;
import com.shirt.pod.model.dto.response.RoleDTO;

import java.util.List;

public interface RoleService {
    List<RoleDTO> getAllRoles();

    RoleDTO getRoleById(Long id);

    RoleDTO createRole(CreateRoleRequest request);

    RoleDTO updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);
}
