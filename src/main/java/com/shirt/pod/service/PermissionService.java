package com.shirt.pod.service;

import com.shirt.pod.model.dto.response.PermissionDTO;

import java.util.List;

public interface PermissionService {
    List<PermissionDTO> getAllPermissions();

    PermissionDTO getPermissionById(Long id);
}
