package com.shirt.pod.model.entity.enums;

public enum StockStatus {
    IN_STOCK,       // Còn hàng (> threshold)
    LOW_STOCK,      // Tồn kho thấp (0 < stock <= threshold)
    OUT_OF_STOCK    // Hết hàng (stock = 0)
}
