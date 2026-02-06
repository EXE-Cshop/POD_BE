package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.response.OrderItemDTO;
import com.shirt.pod.model.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemDTO toDTO(OrderItem orderItem);
}
