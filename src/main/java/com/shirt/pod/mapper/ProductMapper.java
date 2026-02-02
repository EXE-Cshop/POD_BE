package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.CreatePrintAreaRequest;
import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdatePrintAreaRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.model.entity.PrintArea;
import com.shirt.pod.model.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    
    ProductDTO toDTO(BaseProduct product);

    List<ProductDTO> toDTOList(List<BaseProduct> products);

    BaseProduct toEntity(CreateProductRequest request);

    void updateEntity(UpdateProductRequest request, @MappingTarget BaseProduct product);

    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "printAreas", ignore = true)
    ProductDetailDTO toDetailDTO(BaseProduct product);

    @Mapping(target = "baseProductId", source = "baseProduct.id")
    ProductVariantDTO toVariantDTO(ProductVariant variant);

    List<ProductVariantDTO> toVariantDTOList(List<ProductVariant> variants);

    @Mapping(target = "baseProduct", ignore = true)
    ProductVariant toVariantEntity(CreateProductVariantRequest request);

    @Mapping(target = "baseProduct", ignore = true)
    void updateVariantEntity(UpdateProductVariantRequest request, @MappingTarget ProductVariant variant);

  
    @Mapping(target = "baseProductId", source = "baseProduct.id")
    PrintAreaDTO toPrintAreaDTO(PrintArea printArea);

    List<PrintAreaDTO> toPrintAreaDTOList(List<PrintArea> printAreas);

    @Mapping(target = "baseProduct", ignore = true)
    PrintArea toPrintAreaEntity(CreatePrintAreaRequest request);

    @Mapping(target = "baseProduct", ignore = true)
    void updatePrintAreaEntity(UpdatePrintAreaRequest request, @MappingTarget PrintArea printArea);
}
