package com.shirt.pod.controller;

import com.shirt.pod.model.dto.request.RenderPrintRequest;
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

    /**
     * Render production print file (transparent PNG) using mm-based layers.
     */
    @PostMapping("/print")
    public ApiResponse<RenderResponse> renderPrintFile(@Valid @RequestBody RenderPrintRequest request) {
        RenderResponse response = renderEngineService.renderPrintFile(request);
        return ApiResponse.<RenderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Print file rendered successfully")
                .data(response)
                .build();
    }
}
