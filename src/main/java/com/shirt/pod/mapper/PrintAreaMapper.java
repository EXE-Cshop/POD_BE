package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.request.PrintAreaCreateRequest;
import com.shirt.pod.model.dto.request.PrintAreaUpdateRequest;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.model.entity.PrintArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrintAreaMapper {

    @Mapping(target = "baseProductId", source = "baseProduct.id")
    @Mapping(target = "baseProductName", source = "baseProduct.name")
    @Mapping(target = "nameDisplay", source = "name", qualifiedByName = "getDisplayName")
    PrintAreaDTO toDTO(PrintArea printArea);

    List<PrintAreaDTO> toDTOList(List<PrintArea> printAreas);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "baseProduct", ignore = true)
//    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "createdBy", ignore = true)
    PrintArea toEntity(PrintAreaCreateRequest request);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "baseProduct", ignore = true)
//    @Mapping(target = "createdDate", ignore = true)
//    @Mapping(target = "createdBy", ignore = true)
    void updateEntity(PrintAreaUpdateRequest request, @MappingTarget PrintArea printArea);

    @Named("getDisplayName")
    default String getDisplayName(com.shirt.pod.model.entity.enums.PrintAreaName name) {
        return name != null ? name.getDisplayName() : null;
    }
}
