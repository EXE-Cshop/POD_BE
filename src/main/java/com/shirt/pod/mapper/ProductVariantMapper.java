package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductVariantMapper {

    @Mapping(target = "baseProductId", source = "product.id")
    @Mapping(target = "baseProductName", source = "product.name")
    ProductVariantDTO toDTO(ProductVariant variant);

    List<ProductVariantDTO> toDTOList(List<ProductVariant> variants);

    ProductVariant toEntity(CreateProductVariantRequest request);

    void updateEntity(UpdateProductVariantRequest request, @MappingTarget ProductVariant variant);
}
