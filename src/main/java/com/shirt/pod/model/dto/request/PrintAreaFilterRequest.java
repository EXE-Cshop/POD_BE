package com.shirt.pod.model.dto.request;

import com.shirt.pod.model.entity.enums.PrintAreaName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Request object for filtering and pagination of PrintArea
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PrintAreaFilterRequest {

    // Filter fields
    Long baseProductId;
    PrintAreaName name;

    // Pagination fields
    @Builder.Default
    Integer page = 1;

    @Builder.Default
    Integer pageSize = 10;

    @Builder.Default
    String sortBy = "createdDate";

    @Builder.Default
    String order = "DESC";
}
