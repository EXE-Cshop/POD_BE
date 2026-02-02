package com.shirt.pod.model.entity.enums;

import lombok.Getter;

@Getter
public enum PrintAreaName {
    // Vị trí chính
    FRONT_CENTER("Mặt trước - Chính diện"),
    BACK_CENTER("Mặt sau - Chính diện"),

    // Vị trí ngực
    LEFT_CHEST("Ngực trái"),
    RIGHT_CHEST("Ngực phải"),

    // Vị trí tay áo
    LEFT_SLEEVE("Tay áo trái"),
    RIGHT_SLEEVE("Tay áo phải"),

    // Vị trí phụ (thường cho Hoodie hoặc Local Brand)
    BACK_NECK("Sau gáy"),
    BOTTOM_HEM("Gấu áo");

    private final String displayName;

    PrintAreaName(String displayName) {
        this.displayName = displayName;
    }

}
