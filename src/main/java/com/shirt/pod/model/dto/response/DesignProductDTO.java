package com.shirt.pod.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DesignProductDTO {

    Long id;
    Long userId;
    String creatorName;
    Long baseProductId;
    String baseProductName;
    String name;
    Map<String, Object> designJsonData;
    String previewImageUrl;
    Boolean isPublic;
    Instant createdDate;
}
