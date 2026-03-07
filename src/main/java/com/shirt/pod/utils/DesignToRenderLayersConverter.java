package com.shirt.pod.utils;

import com.shirt.pod.model.dto.request.PrintDesignLayerRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts design JSON (Fabric.js serialized) to PrintDesignLayerRequest for render.
 */
public final class DesignToRenderLayersConverter {

    private static final BigDecimal PRINT_AREA_WIDTH_MM = new BigDecimal("100");
    private static final BigDecimal PRINT_AREA_HEIGHT_MM = new BigDecimal("150");

    private DesignToRenderLayersConverter() {}

    @SuppressWarnings("unchecked")
    public static List<PrintDesignLayerRequest> convert(Map<String, Object> designJson, String side) {
        List<PrintDesignLayerRequest> result = new ArrayList<>();
        String arrayKey = "front".equals(side) ? "frontDesign" : "backDesign";
        Object arr = designJson.get(arrayKey);
        if (!(arr instanceof List<?> list)) return result;

        Map<String, Object> printArea = getPrintArea(designJson);
        double paLeft = toDouble(printArea.get("left"), 247.5);
        double paTop = toDouble(printArea.get("top"), 205.0);
        double paWidth = toDouble(printArea.get("width"), 305.0);
        double paHeight = toDouble(printArea.get("height"), 500.0);
        BigDecimal pxToMmX = PRINT_AREA_WIDTH_MM.divide(BigDecimal.valueOf(paWidth), 6, RoundingMode.HALF_UP);
        BigDecimal pxToMmY = PRINT_AREA_HEIGHT_MM.divide(BigDecimal.valueOf(paHeight), 6, RoundingMode.HALF_UP);

        int idx = 0;
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> objMap)) continue;
            Map<String, Object> obj = (Map<String, Object>) objMap;
            PrintDesignLayerRequest layer = toLayer(obj, idx++, paLeft, paTop, pxToMmX, pxToMmY);
            if (layer != null) result.add(layer);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getPrintArea(Map<String, Object> designJson) {
        Object pa = designJson.get("printArea");
        return (pa instanceof Map) ? (Map<String, Object>) pa : Map.of();
    }

    private static PrintDesignLayerRequest toLayer(Map<String, Object> obj, int idx,
                                                   double paLeft, double paTop, BigDecimal pxToMmX, BigDecimal pxToMmY) {
        String type = String.valueOf(obj.getOrDefault("type", ""));
        double left = toDouble(obj.get("left"), 0);
        double top = toDouble(obj.get("top"), 0);
        double scaleX = toDouble(obj.get("scaleX"), 1);
        double scaleY = toDouble(obj.get("scaleY"), 1);
        String originX = String.valueOf(obj.getOrDefault("originX", "center"));
        String originY = String.valueOf(obj.getOrDefault("originY", "center"));

        double w, h;
        if (Boolean.TRUE.equals(obj.get("_scaledDimensions"))) {
            w = toDouble(obj.get("width"), 1);
            h = toDouble(obj.get("height"), 1);
        } else {
            double baseW = "textbox".equals(type)
                    ? toDouble(obj.get("width"), 100)
                    : toDouble(obj.get("width"), 1);
            double baseH = "textbox".equals(type)
                    ? toDouble(obj.get("fontSize"), 24) * 1.5
                    : toDouble(obj.get("height"), 1);
            w = baseW * scaleX;
            h = baseH * scaleY;
        }

        double px = left;
        double py = top;
        if ("center".equals(originX)) px -= w / 2;
        else if ("right".equals(originX)) px -= w;
        if ("center".equals(originY)) py -= h / 2;
        else if ("bottom".equals(originY)) py -= h;

        BigDecimal xMm = BigDecimal.valueOf((px - paLeft) * pxToMmX.doubleValue()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal yMm = BigDecimal.valueOf((py - paTop) * pxToMmY.doubleValue()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal widthMm = BigDecimal.valueOf(Math.max(0.1, w) * pxToMmX.doubleValue()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal heightMm = BigDecimal.valueOf(Math.max(0.1, h) * pxToMmY.doubleValue()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal rotationDeg = BigDecimal.valueOf(toDouble(obj.get("angle"), 0)).setScale(2, RoundingMode.HALF_UP);

        String layerType = "textbox".equals(type) ? "text" : "image";
        PrintDesignLayerRequest.PrintDesignLayerRequestBuilder b = PrintDesignLayerRequest.builder()
                .type(layerType)
                .xMm(xMm)
                .yMm(yMm)
                .widthMm(widthMm)
                .heightMm(heightMm)
                .rotationDeg(rotationDeg)
                .zIndex(idx);

        if ("textbox".equals(type)) {
            b.text(String.valueOf(obj.getOrDefault("text", "")))
                    .fontFamily(String.valueOf(obj.getOrDefault("fontFamily", "Arial")))
                    .fontSize(toInt(obj.get("fontSize"), 24))
                    .fontColor(String.valueOf(obj.getOrDefault("fill", "#000000")));
        } else {
            String url = String.valueOf(obj.getOrDefault("src", ""));
            b.url(url != null && !url.equals("null") ? url : "")
                    .opacity(BigDecimal.valueOf(toDouble(obj.get("opacity"), 1)));
        }
        return b.build();
    }

    private static double toDouble(Object o, double fallback) {
        if (o == null) return fallback;
        if (o instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(o));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int toInt(Object o, int fallback) {
        if (o == null) return fallback;
        if (o instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
