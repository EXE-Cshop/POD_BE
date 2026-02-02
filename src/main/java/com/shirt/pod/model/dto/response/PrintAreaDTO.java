package com.shirt.pod.model.dto.response;

import com.shirt.pod.model.entity.enums.PrintAreaName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PrintAreaDTO implements Serializable {

    Long id;
    Long baseProductId;
    String baseProductName;
    PrintAreaName name;
    String nameDisplay; // Display name from enum
    BigDecimal widthMm;
    BigDecimal heightMm;
    Double topOffsetPercent;
    Double leftOffsetPercent;
    Double widthPercent;
    Double heightPercent;
    String maskImageUrl;
    Instant createdDate;
    String createdBy;
}
