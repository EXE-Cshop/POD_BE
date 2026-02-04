package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.RenderRequest;
import com.shirt.pod.model.dto.response.ApiResponse;
import com.shirt.pod.model.dto.response.RenderResponse;
import com.shirt.pod.service.RenderEngineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/renders")
@RequiredArgsConstructor
public class RenderController {

    private final RenderEngineService renderEngineService;

    @PostMapping
    public ApiResponse<RenderResponse> renderDesign(@Valid @RequestBody RenderRequest request) {
        RenderResponse response = renderEngineService.renderDesign(request);
        return ApiResponse.<RenderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Render completed successfully")
                .data(response)
                .build();
    }
}
