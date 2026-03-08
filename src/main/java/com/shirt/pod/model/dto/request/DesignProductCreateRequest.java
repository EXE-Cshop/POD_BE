package com.shirt.pod.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DesignProductCreateRequest {

    @NotBlank(message = "Tên thiết kế không được để trống")
    @Size(max = 255)
    String name;

    @NotNull(message = "Dữ liệu thiết kế không được để trống")
    Map<String, Object> designJsonData;

    @Size(max = 2048)
    String garmentImageUrl;

    Long baseProductId;

    @Builder.Default
    Boolean isPublic = false;
}
