package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.BaseProductCreateRequest;
import com.shirt.pod.model.dto.request.BaseProductUpdateRequest;
import com.shirt.pod.model.dto.response.BaseProductDTO;
import com.shirt.pod.model.entity.BaseProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BaseProductMapper {

    BaseProductDTO toDTO(BaseProduct baseProduct);

    List<BaseProductDTO> toDTOList(List<BaseProduct> baseProducts);

    BaseProduct toEntity(BaseProductCreateRequest request);

    void updateEntity(BaseProductUpdateRequest request, @MappingTarget BaseProduct baseProduct);
}
