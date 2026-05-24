package com.shirt.pod.model.entity.enums;

/**
 * Enum chứa tất cả permissions của hệ thống.
 * Mỗi khi thêm tính năng mới, developer cần thêm permission tương ứng vào đây.
 * Hệ thống sẽ tự động đồng bộ vào Database khi khởi động.
 */
public enum PermissionName {
    // User Management
    USER_VIEW,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,

    // Role Management
    ROLE_VIEW,
    ROLE_CREATE,
    ROLE_UPDATE,
    ROLE_DELETE,

    // Permission Management
    PERMISSION_VIEW,

    // Product Management
    PRODUCT_VIEW,
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Product Variant Management
    VARIANT_VIEW,
    VARIANT_CREATE,
    VARIANT_UPDATE,
    VARIANT_DELETE,

    // Inventory Management
    INVENTORY_VIEW,
    INVENTORY_UPDATE,

    // Dashboard
    DASHBOARD_VIEW,

    // Order Management
    ORDER_VIEW,
    ORDER_CREATE,
    ORDER_UPDATE,
    ORDER_DELETE,
    ORDER_APPROVE,

    // Category Management
    CATEGORY_VIEW,
    CATEGORY_CREATE,
    CATEGORY_UPDATE,
    CATEGORY_DELETE,

    // Review Management
    REVIEW_DELETE,

    // Promotion Management
    PROMOTION_VIEW,
    PROMOTION_CREATE,
    PROMOTION_UPDATE,
    PROMOTION_DELETE,

    // Wishlist Management
    WISHLIST_VIEW
}
