package com.shirt.pod.model.dto.request;

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
public class DesignProductUpdateRequest {

    @Size(max = 255)
    String name;

    Boolean isPublic;

    /** Optional: full design JSON to update content. When set, re-renders preview. */
    Map<String, Object> designJsonData;

    @Size(max = 2048)
    String garmentImageUrl;
}
