package com.shirt.pod.model.dto.request;

import com.shirt.pod.model.entity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateUserRequest implements Serializable {

    @Email(message = "Email should be valid")
    String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phoneNumber;

    String avatarUrl;
    UserStatus status;
    Set<Long> roleIds;
}
