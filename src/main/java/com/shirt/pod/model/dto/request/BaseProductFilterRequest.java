package com.shirt.pod.model.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BaseProductFilterRequest {

    // Filter fields
    String name;
    String material;
    String printTechnology;
    Boolean active;

    // Pagination fields
    @Builder.Default
    Integer page = 1;

    @Builder.Default
    Integer size = 10;

    @Builder.Default
    String sortBy = "created_date";

    @Builder.Default
    String order = "DESC";
}
