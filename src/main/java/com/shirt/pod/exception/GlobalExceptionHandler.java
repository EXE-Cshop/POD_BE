package com.shirt.pod.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shirt.pod.model.dto.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(exception.getFormattedMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    // lỗi thì bắt ở data nhé
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingMethodNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        ApiResponse apiResponse = ApiResponse.builder()
                .code(400)
                .message("validate error")
                .data(errors)
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(404).body(apiResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(400)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
