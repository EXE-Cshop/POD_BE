package com.shirt.pod.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class InventorySummaryDTO {

    Long totalProducts;           
    Long totalVariants;           
    Long inStockVariants;         
    Long outOfStockVariants;      
    Long lowStockVariants;         
    BigDecimal totalStockValue;    
}
