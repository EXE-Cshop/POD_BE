package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.PrintAreaCreateRequest;
import com.shirt.pod.model.dto.request.PrintAreaFilterRequest;
import com.shirt.pod.model.dto.request.PrintAreaUpdateRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.service.PrintAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.shirt.pod.security.SecurityConstants;

@RestController
@RequestMapping("/api/v1/print-areas")
@RequiredArgsConstructor
@Tag(name = "Print Area", description = "APIs for managing print areas on products")
public class PrintAreaController {

    private final PrintAreaService printAreaService;

    @Operation(summary = "Get list of print areas", description = "Retrieve a paginated list of print areas with optional filters. Use request body to pass filter parameters.")
    @GetMapping
    @PreAuthorize("hasAuthority('" + SecurityConstants.PRINT_AREA_VIEW + "')")
    public ApiResponse<Page<PrintAreaDTO>> getAll(@ModelAttribute PrintAreaFilterRequest filterRequest) {
        return ApiResponse.<Page<PrintAreaDTO>>builder()
                .code(HttpStatus.OK.value())
                .message("Print areas fetched successfully")
                .data(printAreaService.getAll(filterRequest))
                .build();
    }

    @Operation(summary = "Get print area by ID", description = "Retrieve a single print area by its ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + SecurityConstants.PRINT_AREA_VIEW + "')")
    public ApiResponse<PrintAreaDTO> getById(@PathVariable Long id) {
        return ApiResponse.<PrintAreaDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Print area fetched successfully")
                .data(printAreaService.getById(id))
                .build();
    }

    @Operation(summary = "Create new print area", description = "Create a new print area for a base product")
    @PostMapping
    @PreAuthorize("hasAuthority('" + SecurityConstants.PRINT_AREA_CREATE + "')")
    public ApiResponse<PrintAreaDTO> create(@Valid @RequestBody PrintAreaCreateRequest request) {
        return ApiResponse.<PrintAreaDTO>builder()
                .code(HttpStatus.CREATED.value())
                .message("Print area created successfully")
                .data(printAreaService.create(request))
                .build();
    }

    @Operation(summary = "Update print area", description = "Update an existing print area. Supports partial updates - only provided fields will be updated")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + SecurityConstants.PRINT_AREA_UPDATE + "')")
    public ApiResponse<PrintAreaDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PrintAreaUpdateRequest request) {
        return ApiResponse.<PrintAreaDTO>builder()
                .code(HttpStatus.OK.value())
                .message("Print area updated successfully")
                .data(printAreaService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete print area", description = "Delete a print area")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + SecurityConstants.PRINT_AREA_DELETE + "')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        printAreaService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Print area deleted successfully")
                .build();
    }
}
