package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.CreateUserRequest;
import com.shirt.pod.model.dto.response.UserDTO;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRolesToNames(user.getRoles()))")
    UserDTO toDTO(User user);

//    @Mapping(target = "provider", ignore = true)
//    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", source = "password")
    User toEntity(CreateUserRequest request);

    List<UserDTO> toDTOList(List<User> users);

    default List<String> mapRolesToNames(Set<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
