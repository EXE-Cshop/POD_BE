package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.ProductVariantCreateRequest;
import com.shirt.pod.model.dto.request.ProductVariantUpdateRequest;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductVariantMapper {

    @Mapping(target = "baseProductId", source = "baseProduct.id")
    @Mapping(target = "baseProductName", source = "baseProduct.name")
    ProductVariantDTO toDTO(ProductVariant variant);

    List<ProductVariantDTO> toDTOList(List<ProductVariant> variants);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "baseProduct", ignore = true)
//    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "createdBy", ignore = true)
    ProductVariant toEntity(ProductVariantCreateRequest request);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "baseProduct", ignore = true)
//    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(ProductVariantUpdateRequest request, @MappingTarget ProductVariant variant);
}
