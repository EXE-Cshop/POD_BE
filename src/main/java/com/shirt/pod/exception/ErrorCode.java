package com.shirt.pod.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

  // ========== 10xx: Authentication Errors ==========
  INVALID_CREDENTIALS(1001, "Invalid email or password", HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN(1002, "Token is invalid", HttpStatus.UNAUTHORIZED),
  EXPIRED_TOKEN(1003, "Token has expired", HttpStatus.UNAUTHORIZED),
  TOKEN_NOT_VERIFIED(1004, "Token is not verified", HttpStatus.UNAUTHORIZED),
  INVALID_OTP(1005, "OTP is invalid", HttpStatus.UNAUTHORIZED),
  EXPIRED_OTP(1006, "OTP has expired", HttpStatus.UNAUTHORIZED),
  PASSWORD_INCORRECT(1007, "Password is incorrect", HttpStatus.UNAUTHORIZED),
  OLD_PASSWORD_NOT_MATCH(1008, "Old password does not match", HttpStatus.UNAUTHORIZED),
  ACCOUNT_LOCKED(1009, "Account has been locked", HttpStatus.UNAUTHORIZED),
  ACCOUNT_NOT_VERIFIED(1010, "Account has not been verified", HttpStatus.UNAUTHORIZED),
  SESSION_EXPIRED(1011, "Session has expired", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_INVALID(1012, "Refresh token is invalid", HttpStatus.UNAUTHORIZED),

  // ========== 11xx: Authorization Errors ==========
  ACCESS_DENIED(1101, "Access denied", HttpStatus.FORBIDDEN),
  INSUFFICIENT_PERMISSION(1102, "Insufficient permissions to perform this action", HttpStatus.FORBIDDEN),
  UNAUTHENTICATED(1103, "User is not authenticated", HttpStatus.UNAUTHORIZED),
  ROLE_NOT_ALLOWED(1104, "Your role is not allowed to access this resource", HttpStatus.FORBIDDEN),
  RESOURCE_FORBIDDEN(1105, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),

  // ========== 20xx: Validation Errors ==========
  INVALID_EMAIL(2001, "Email format is invalid", HttpStatus.BAD_REQUEST),
  INVALID_PHONE(2002, "Phone number format is invalid", HttpStatus.BAD_REQUEST),
  REQUIRED_FIELD_MISSING(2003, "Required field {0} is missing", HttpStatus.BAD_REQUEST),
  INVALID_INPUT(2004, "Invalid input for field {0}", HttpStatus.BAD_REQUEST),
  INVALID_DATE_FORMAT(2005, "Invalid date format. Expected: {0}", HttpStatus.BAD_REQUEST),
  INVALID_PRICE(2006, "Price must be greater than 0", HttpStatus.BAD_REQUEST),
  INVALID_QUANTITY(2007, "Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
  INVALID_IMAGE_URL(2008, "Invalid image URL format", HttpStatus.BAD_REQUEST),
  INVALID_COLOR_CODE(2009, "Invalid color code format. Expected hex format", HttpStatus.BAD_REQUEST),
  INVALID_SIZE(2010, "Invalid size value", HttpStatus.BAD_REQUEST),
  PASSWORD_TOO_WEAK(2011, "Password is too weak. Must contain at least 8 characters", HttpStatus.BAD_REQUEST),
  INVALID_ENUM_VALUE(2012, "Invalid value for {0}. Allowed values: {1}", HttpStatus.BAD_REQUEST),
  INVALID_PAGINATION(2013, "Invalid pagination parameters. Page: {0}, Size: {1}", HttpStatus.BAD_REQUEST),
  INVALID_LAYER_TYPE(2014, "Invalid layer type: {0}. Allowed: image, text", HttpStatus.BAD_REQUEST),

  // ========== 30xx: Resource Not Found ==========
  USER_NOT_FOUND(3001, "User not found with {0}: {1}", HttpStatus.NOT_FOUND),
  PRODUCT_NOT_FOUND(3002, "Product not found with {0}: {1}", HttpStatus.NOT_FOUND),
  ORDER_NOT_FOUND(3003, "Order not found with {0}: {1}", HttpStatus.NOT_FOUND),
  CATEGORY_NOT_FOUND(3004, "Category not found with {0}: {1}", HttpStatus.NOT_FOUND),
  ROLE_NOT_FOUND(3005, "Role not found with {0}: {1}", HttpStatus.NOT_FOUND),
  PERMISSION_NOT_FOUND(3006, "Permission not found with {0}: {1}", HttpStatus.NOT_FOUND),
  DESIGN_NOT_FOUND(3007, "Design not found with {0}: {1}", HttpStatus.NOT_FOUND),
  VARIANT_NOT_FOUND(3008, "Product variant not found with {0}: {1}", HttpStatus.NOT_FOUND),
  PRINT_AREA_NOT_FOUND(3009, "Print area not found with {0}: {1}", HttpStatus.NOT_FOUND),
  ORDER_ITEM_NOT_FOUND(3010, "Order item not found with {0}: {1}", HttpStatus.NOT_FOUND),
  PAYMENT_NOT_FOUND(3011, "Payment not found with {0}: {1}", HttpStatus.NOT_FOUND),
  SHIPPING_ADDRESS_NOT_FOUND(3012, "Shipping address not found with {0}: {1}", HttpStatus.NOT_FOUND),
  COUPON_NOT_FOUND(3013, "Coupon not found with code: {0}", HttpStatus.NOT_FOUND),
  REVIEW_NOT_FOUND(3014, "Review not found with {0}: {1}", HttpStatus.NOT_FOUND),

  // ========== 40xx: Business Logic Errors ==========
  EMAIL_ALREADY_EXISTS(4001, "Email already exists: {0}", HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS(4002, "Username already exists: {0}", HttpStatus.BAD_REQUEST),
  PHONE_ALREADY_EXISTS(4003, "Phone number already exists: {0}", HttpStatus.BAD_REQUEST),
  SKU_ALREADY_EXISTS(4004, "SKU already exists: {0}", HttpStatus.BAD_REQUEST),
  COUPON_CODE_ALREADY_EXISTS(4005, "Coupon code already exists: {0}", HttpStatus.BAD_REQUEST),

  INSUFFICIENT_STOCK(4010, "Insufficient stock for {0}. Available: {1}, Requested: {2}", HttpStatus.BAD_REQUEST),
  PRODUCT_OUT_OF_STOCK(4011, "Product {0} is out of stock", HttpStatus.BAD_REQUEST),
  VARIANT_OUT_OF_STOCK(4012, "Product variant {0} is out of stock", HttpStatus.BAD_REQUEST),

  ORDER_ALREADY_CANCELLED(4020, "Order {0} has already been cancelled", HttpStatus.BAD_REQUEST),
  ORDER_ALREADY_COMPLETED(4021, "Order {0} has already been completed", HttpStatus.BAD_REQUEST),
  ORDER_ALREADY_SHIPPED(4022, "Order {0} has already been shipped", HttpStatus.BAD_REQUEST),
  ORDER_CANNOT_BE_CANCELLED(4023, "Order {0} cannot be cancelled in current status: {1}", HttpStatus.BAD_REQUEST),
  ORDER_CANNOT_BE_MODIFIED(4024, "Order {0} cannot be modified in current status: {1}", HttpStatus.BAD_REQUEST),
  EMPTY_CART(4025, "Cart is empty. Please add items before checkout", HttpStatus.BAD_REQUEST),

  USER_ALREADY_DELETED(4030, "User has been deleted", HttpStatus.BAD_REQUEST),
  USER_ALREADY_BANNED(4031, "User has been banned", HttpStatus.BAD_REQUEST),
  USER_INACTIVE(4032, "User account is inactive", HttpStatus.BAD_REQUEST),

  PRODUCT_ALREADY_INACTIVE(4040, "Product {0} is inactive", HttpStatus.BAD_REQUEST),
  PRODUCT_ALREADY_DELETED(4041, "Product {0} has been deleted", HttpStatus.BAD_REQUEST),
  VARIANT_ALREADY_INACTIVE(4042, "Product variant {0} is inactive", HttpStatus.BAD_REQUEST),

  DUPLICATE_NAME(4050, "Name {0} already exists", HttpStatus.BAD_REQUEST),
  DUPLICATE_DESIGN(4051, "Design with same configuration already exists", HttpStatus.BAD_REQUEST),

  COUPON_EXPIRED(4060, "Coupon {0} has expired", HttpStatus.BAD_REQUEST),
  COUPON_NOT_STARTED(4061, "Coupon {0} is not yet active", HttpStatus.BAD_REQUEST),
  COUPON_USAGE_LIMIT_REACHED(4062, "Coupon {0} has reached its usage limit", HttpStatus.BAD_REQUEST),
  COUPON_MIN_ORDER_NOT_MET(4063, "Minimum order amount {0} not met for coupon {1}", HttpStatus.BAD_REQUEST),
  COUPON_NOT_APPLICABLE(4064, "Coupon {0} is not applicable to this order", HttpStatus.BAD_REQUEST),

  PAYMENT_FAILED(4070, "Payment of {0} failed. Reason: {1}", HttpStatus.BAD_REQUEST),
  PAYMENT_ALREADY_PROCESSED(4071, "Payment for order {0} has already been processed", HttpStatus.BAD_REQUEST),
  PAYMENT_AMOUNT_MISMATCH(4072, "Payment amount mismatch. Expected: {0}, Received: {1}", HttpStatus.BAD_REQUEST),
  REFUND_NOT_ALLOWED(4073, "Refund is not allowed for order {0}", HttpStatus.BAD_REQUEST),
  REFUND_AMOUNT_EXCEEDS(4074, "Refund amount {0} exceeds paid amount {1}", HttpStatus.BAD_REQUEST),

  DESIGN_SIZE_EXCEEDS_LIMIT(4080, "Design size exceeds print area limit. Max: {0}x{1}, Actual: {2}x{3}",
      HttpStatus.BAD_REQUEST),
  DESIGN_RESOLUTION_TOO_LOW(4081, "Design resolution is too low. Minimum: {0} DPI", HttpStatus.BAD_REQUEST),
  INVALID_DESIGN_POSITION(4082, "Design position is outside print area boundaries", HttpStatus.BAD_REQUEST),

  ROLE_REPOSITORY_EMPTY(4090, "Role repository is empty", HttpStatus.BAD_REQUEST),
  PERMISSION_REPOSITORY_EMPTY(4091, "Permission repository is empty", HttpStatus.BAD_REQUEST),
  CANNOT_DELETE_DEFAULT_ROLE(4092, "Cannot delete default role: {0}", HttpStatus.BAD_REQUEST),
  ROLE_IN_USE(4093, "Role {0} is currently in use and cannot be deleted", HttpStatus.BAD_REQUEST),

  // ========== 50xx: External Service Errors ==========
  KEYCLOAK_USER_CREATION_FAILED(5001, "Failed to create user in Keycloak", HttpStatus.INTERNAL_SERVER_ERROR),
  KEYCLOAK_TOKEN_ERROR(5002, "Failed to get Keycloak admin token", HttpStatus.INTERNAL_SERVER_ERROR),
  KEYCLOAK_SERVICE_UNAVAILABLE(5003, "Keycloak service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),

  PAYMENT_GATEWAY_ERROR(5010, "Payment gateway error: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  PAYMENT_GATEWAY_TIMEOUT(5011, "Payment gateway timeout", HttpStatus.GATEWAY_TIMEOUT),
  PAYMENT_GATEWAY_UNAVAILABLE(5012, "Payment gateway is unavailable", HttpStatus.SERVICE_UNAVAILABLE),

  EMAIL_SERVICE_ERROR(5020, "Failed to send email to {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  EMAIL_SERVICE_UNAVAILABLE(5021, "Email service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  SMS_SERVICE_ERROR(5022, "Failed to send SMS to {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  SMS_SERVICE_UNAVAILABLE(5023, "SMS service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),

  SHIPPING_SERVICE_ERROR(5030, "Shipping service error: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  SHIPPING_SERVICE_UNAVAILABLE(5031, "Shipping service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  SHIPPING_RATE_CALCULATION_FAILED(5032, "Failed to calculate shipping rate", HttpStatus.INTERNAL_SERVER_ERROR),
  TRACKING_NUMBER_GENERATION_FAILED(5033, "Failed to generate tracking number", HttpStatus.INTERNAL_SERVER_ERROR),

  PRINT_SERVICE_ERROR(5040, "Print service error: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  PRINT_SERVICE_UNAVAILABLE(5041, "Print service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  PRINT_JOB_FAILED(5042, "Print job failed for order {0}", HttpStatus.INTERNAL_SERVER_ERROR),

  STORAGE_SERVICE_ERROR(5050, "Storage service error: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  STORAGE_SERVICE_UNAVAILABLE(5051, "Storage service is unavailable", HttpStatus.SERVICE_UNAVAILABLE),

  // ========== 60xx: File/Upload Errors ==========
  FILE_UPLOAD_FAILED(6001, "Failed to upload file: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_FILE_FORMAT(6002, "Invalid file format. Expected: {0}, Got: {1}", HttpStatus.BAD_REQUEST),
  FILE_TOO_LARGE(6003, "File size exceeds limit. Max: {0}MB, Actual: {1}MB", HttpStatus.BAD_REQUEST),
  FILE_TOO_SMALL(6004, "File size is too small. Min: {0}KB", HttpStatus.BAD_REQUEST),
  FILE_NOT_FOUND(6005, "File not found: {0}", HttpStatus.NOT_FOUND),
  FILE_CORRUPTED(6006, "File is corrupted or cannot be read", HttpStatus.BAD_REQUEST),

  IMAGE_PROCESSING_FAILED(6010, "Failed to process image: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  IMAGE_RESOLUTION_TOO_LOW(6011, "Image resolution is too low. Minimum: {0}x{1} pixels", HttpStatus.BAD_REQUEST),
  IMAGE_RESOLUTION_TOO_HIGH(6012, "Image resolution is too high. Maximum: {0}x{1} pixels", HttpStatus.BAD_REQUEST),
  INVALID_IMAGE_DIMENSIONS(6013, "Invalid image dimensions. Expected ratio: {0}", HttpStatus.BAD_REQUEST),

  RENDER_FAILED(6014, "Render failed: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  IMAGE_READ_FAILED(6015, "Failed to read image from URL: {0}", HttpStatus.BAD_REQUEST),
  IMAGE_RENDER_FAILED(6016, "Failed to render image layer: {0}", HttpStatus.INTERNAL_SERVER_ERROR),
  SAVE_IMAGE_FAILED(6017, "Failed to save rendered image: {0}", HttpStatus.INTERNAL_SERVER_ERROR),

  EXCEL_HEADER_MISSING(6020, "Excel file is missing required headers: {0}", HttpStatus.BAD_REQUEST),
  EXCEL_PARSING_ERROR(6021, "Failed to parse Excel file at row {0}", HttpStatus.BAD_REQUEST),
  CSV_PARSING_ERROR(6022, "Failed to parse CSV file at row {0}", HttpStatus.BAD_REQUEST),
  INVALID_FILE_ENCODING(6023, "Invalid file encoding. Expected: {0}", HttpStatus.BAD_REQUEST),

  // ========== 70xx: Database/Data Errors ==========
  DATABASE_ERROR(7001, "Database error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
  DATABASE_CONNECTION_FAILED(7002, "Failed to connect to database", HttpStatus.INTERNAL_SERVER_ERROR),
  DUPLICATE_KEY_ERROR(7003, "Duplicate key error: {0}", HttpStatus.CONFLICT),
  FOREIGN_KEY_VIOLATION(7004, "Foreign key constraint violation: {0}", HttpStatus.BAD_REQUEST),
  DATA_INTEGRITY_VIOLATION(7005, "Data integrity violation: {0}", HttpStatus.BAD_REQUEST),
  OPTIMISTIC_LOCK_ERROR(7006, "Data has been modified by another user. Please refresh and try again",
      HttpStatus.CONFLICT),
  TRANSACTION_FAILED(7007, "Transaction failed: {0}", HttpStatus.INTERNAL_SERVER_ERROR),

  // ========== 80xx: Rate Limiting & Quota Errors ==========
  RATE_LIMIT_EXCEEDED(8001, "Rate limit exceeded. Please try again in {0} seconds", HttpStatus.TOO_MANY_REQUESTS),
  DAILY_QUOTA_EXCEEDED(8002, "Daily quota exceeded for {0}", HttpStatus.TOO_MANY_REQUESTS),
  MONTHLY_QUOTA_EXCEEDED(8003, "Monthly quota exceeded for {0}", HttpStatus.TOO_MANY_REQUESTS),
  CONCURRENT_REQUEST_LIMIT(8004, "Too many concurrent requests. Please try again later", HttpStatus.TOO_MANY_REQUESTS),
  API_QUOTA_EXCEEDED(8005, "API quota exceeded. Limit: {0} requests per {1}", HttpStatus.TOO_MANY_REQUESTS),

  // ========== 90xx: System Errors ==========
  UNKNOWN_ERROR(9001, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
  INTERNAL_SERVER_ERROR(9002, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
  SERVICE_UNAVAILABLE(9003, "Service is temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
  MAINTENANCE_MODE(9004, "System is under maintenance. Please try again later", HttpStatus.SERVICE_UNAVAILABLE),
  CONFIGURATION_ERROR(9005, "System configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
  FEATURE_NOT_IMPLEMENTED(9006, "Feature {0} is not yet implemented", HttpStatus.NOT_IMPLEMENTED),
  DEPRECATED_API(9007, "This API endpoint is deprecated. Please use {0} instead", HttpStatus.GONE),
  TIMEOUT_ERROR(9008, "Request timeout. Please try again", HttpStatus.REQUEST_TIMEOUT),
  CIRCUIT_BREAKER_OPEN(9009, "Service circuit breaker is open. Please try again later", HttpStatus.SERVICE_UNAVAILABLE);

  int code;
  String message;
  HttpStatusCode httpStatusCode;
}
