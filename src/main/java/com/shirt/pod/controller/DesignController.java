package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.CreateDesignRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.DashboardStatsDTO;
import com.shirt.pod.model.entity.Design;
import com.shirt.pod.service.DesignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/designs")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<Design>>> getPublicFeed(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Design> feed = designService.getPublicFeed(pageable);
        ApiResponse<Page<Design>> response = ApiResponse.<Page<Design>>builder()
                .data(feed)
                .code(HttpStatus.OK.value())
                .message("Get public feed successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);

    }

    @PostMapping
    public ResponseEntity<ApiResponse<Design>> createDesign(@Valid @RequestBody CreateDesignRequest request) {
        Design design = designService.createDesign(request);

        ApiResponse<Design> response = ApiResponse.<Design>builder()
                .data(design)
                .code(HttpStatus.OK.value())
                .message("Create design successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Design>> getDesignById(@PathVariable Long id) {
        Design design = designService.getDesignById(id);
        ApiResponse<Design> response = ApiResponse.<Design>builder()
                .data(design)
                .code(HttpStatus.OK.value())
                .message("Get design successfully")
                .timestamp(Instant.now().toEpochMilli())
                .build();
        return ResponseEntity.ok(response);
    }
}
