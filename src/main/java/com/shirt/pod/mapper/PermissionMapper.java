package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.response.PermissionDTO;
import com.shirt.pod.model.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDTO(Permission permission);
}
