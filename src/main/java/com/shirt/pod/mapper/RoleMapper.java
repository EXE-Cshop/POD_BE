package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.response.RoleDTO;
import com.shirt.pod.model.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
}
