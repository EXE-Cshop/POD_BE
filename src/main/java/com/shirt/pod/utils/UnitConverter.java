package com.shirt.pod.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitConverter {

    private static final BigDecimal MM_PER_INCH = new BigDecimal("25.4");

    public static int mmToPixels(BigDecimal mm, int dpi) {
        BigDecimal inches = mm.divide(MM_PER_INCH, 4, RoundingMode.HALF_UP);
        BigDecimal pixels = inches.multiply(new BigDecimal(dpi));
        return pixels.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public static BigDecimal pixelsToMm(int pixels, int dpi) {
        BigDecimal inches = new BigDecimal(pixels)
                .divide(new BigDecimal(dpi), 4, RoundingMode.HALF_UP);
        return inches.multiply(MM_PER_INCH)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static double degreeToRadian(BigDecimal degree) {
        return Math.toRadians(degree.doubleValue());
    }

    public static int[] parseHexColor(String hexColor) {
        if (hexColor == null || !hexColor.startsWith("#")) {
            return new int[]{255, 255, 255}; // Default white
        }

        String hex = hexColor.substring(1);
        if (hex.length() != 6) {
            return new int[]{255, 255, 255};
        }

        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new int[]{r, g, b};
        } catch (NumberFormatException e) {
            return new int[]{255, 255, 255};
        }
    }
}
