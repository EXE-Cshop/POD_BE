package com.shirt.pod.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shirt.pod.model.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public class ResponseUtils {
    private ResponseUtils() {
        //co method static
        //chan dung tao instance
        //chi goi method
    }
    public static void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(status)
                .message(message)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
