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

    // Product Management (For general product flows if still needed, else we can keep them)
    PRODUCT_VIEW,
    PRODUCT_CREATE,
    PRODUCT_UPDATE,
    PRODUCT_DELETE,

    // Base Product Management
    BASE_PRODUCT_VIEW,
    BASE_PRODUCT_CREATE,
    BASE_PRODUCT_UPDATE,
    BASE_PRODUCT_DELETE,

    // Product Variant Management
    VARIANT_VIEW,
    VARIANT_CREATE,
    VARIANT_UPDATE,
    VARIANT_DELETE,

    // Print Area Management
    PRINT_AREA_VIEW,
    PRINT_AREA_CREATE,
    PRINT_AREA_UPDATE,
    PRINT_AREA_DELETE,

    // Sticker Management
    STICKER_VIEW,
    STICKER_CREATE,
    STICKER_UPDATE,
    STICKER_DELETE,

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

    // Design Management
    DESIGN_VIEW,
    DESIGN_CREATE,
    DESIGN_UPDATE,
    DESIGN_DELETE
}
