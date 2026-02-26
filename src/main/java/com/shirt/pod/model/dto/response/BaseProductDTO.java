package com.shirt.pod.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BaseProductDTO {

    Long id;
    String name;
    String description;
    BigDecimal basePrice;
    String material;
    String printTechnology;
    Boolean active;
    Instant createdDate;
    Instant modifiedDate;
    String createdBy;
    String modifiedBy;
}
