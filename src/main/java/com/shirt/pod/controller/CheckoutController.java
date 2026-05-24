package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CheckoutRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.OrderDTO;
import com.shirt.pod.security.CustomUserDetails;
import com.shirt.pod.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping({"/api/v1/checkout", "/api/checkout"})
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Checkout APIs")
@PreAuthorize("isAuthenticated()")
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    @Operation(summary = "Checkout current cart")
    public ApiResponse<OrderDTO> checkout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CheckoutRequest request) {
        OrderDTO orderDTO = checkoutService.checkout(userDetails.getId(), request);
        
        return ApiResponse.<OrderDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(orderDTO)
                .build();
    }
}
