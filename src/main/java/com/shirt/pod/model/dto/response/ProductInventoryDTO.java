package com.shirt.pod.model.dto.response;

import com.shirt.pod.model.entity.enums.StockStatus;
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
public class ProductInventoryDTO {

    Long productId;
    String productName;
    Integer totalVariants;        
    Integer inStockVariants;       
    Integer outOfStockVariants;    
    Integer lowStockVariants;      
    Integer totalStockQuantity;    
    StockStatus stockStatus;       
}
