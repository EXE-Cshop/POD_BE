package com.shirt.pod.mapper;

import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.model.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOList(List<Order> orders);
}
