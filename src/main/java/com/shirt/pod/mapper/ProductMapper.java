package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.Product;
import com.shirt.pod.model.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    
    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOList(List<Product> products);

    Product toEntity(CreateProductRequest request);

    void updateEntity(UpdateProductRequest request, @MappingTarget Product product);

    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    ProductDetailDTO toDetailDTO(Product product);

    @Mapping(target = "baseProductId", source = "product.id")
    @Mapping(target = "baseProductName", source = "product.name")
    ProductVariantDTO toVariantDTO(ProductVariant variant);

    List<ProductVariantDTO> toVariantDTOList(List<ProductVariant> variants);

    @Mapping(target = "product", ignore = true)
    ProductVariant toVariantEntity(CreateProductVariantRequest request);

    @Mapping(target = "product", ignore = true)
    void updateVariantEntity(UpdateProductVariantRequest request, @MappingTarget ProductVariant variant);
}
