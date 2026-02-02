package com.shirt.pod.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shirt.pod.model.entity.enums.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserDTO implements Serializable {
    Long id;
    String email;
    String fullName;
    String phoneNumber;
    String avatarUrl;
    UserStatus status;
    List<String> roles;
    Instant createdDate;
    Instant modifiedDate;
    String createdBy;
    String modifiedBy;
}