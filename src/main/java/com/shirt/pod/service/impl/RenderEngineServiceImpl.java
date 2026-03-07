package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.model.dto.request.PrintDesignLayerRequest;
import com.shirt.pod.model.dto.request.RenderPrintRequest;
import com.shirt.pod.model.dto.response.RenderResponse;
import com.shirt.pod.service.RenderEngineService;
import com.shirt.pod.service.UploadService;
import com.shirt.pod.utils.UnitConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenderEngineServiceImpl implements RenderEngineService {

    private static final String LOCAL_RENDER_DIR = "tmp/renders";
    private static final int DEFAULT_DPI = 300;
    private static final BigDecimal MIN_MM = new BigDecimal("0.1");

    private final UploadService uploadService;

    @Override
    public RenderResponse renderPrintFile(RenderPrintRequest request) {
        long startTime = System.currentTimeMillis();
        List<File> tempFiles = new ArrayList<>();

        try {
            log.info("Start renderPrintFile, width_mm={}, height_mm={}, dpi={}, layer_count={}",
                    request.getWidthMm(), request.getHeightMm(), request.getDpi(), 
                    request.getLayers() != null ? request.getLayers().size() : 0);

            if (request.getLayers() == null || request.getLayers().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_INPUT, "layers");
            }
            int dpi = request.getDpi() != null ? request.getDpi() : DEFAULT_DPI;

            BigDecimal widthMm = request.getWidthMm();
            BigDecimal heightMm = request.getHeightMm();
            if (widthMm == null || heightMm == null
                    || widthMm.compareTo(MIN_MM) < 0
                    || heightMm.compareTo(MIN_MM) < 0) {
                throw new AppException(ErrorCode.INVALID_INPUT, "width_mm/height_mm");
            }

            int designWidthPx = UnitConverter.mmToPixels(widthMm, dpi);
            int designHeightPx = UnitConverter.mmToPixels(heightMm, dpi);
            log.debug("Design buffer size (px): {}x{}", designWidthPx, designHeightPx);

            BufferedImage designBuffer = createTransparentCanvas(designWidthPx, designHeightPx);
            Graphics2D g2dDesign = setupGraphics2D(designBuffer);
            renderPrintLayersMm(g2dDesign, request.getLayers(), dpi, tempFiles);
            g2dDesign.dispose();

            BufferedImage canvas;
            if (request.getGarmentImageUrl() != null && !request.getGarmentImageUrl().isBlank()) {
                canvas = compositeGarmentWithDesign(request, designBuffer, designWidthPx, designHeightPx, tempFiles);
            } else {
                canvas = designBuffer;
            }

            // Ghi canvas ra byte[] (PNG)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(canvas, "PNG", baos);
            byte[] bytes = baos.toByteArray();

            String fileUrl;
            long fileSize;

            // Ưu tiên upload qua UploadService (Cloudinary), nếu fail thì fallback lưu local
            try {
                log.info("Uploading rendered preview to Cloudinary, size={} bytes", bytes.length);
                var uploadResult = uploadService.uploadImageBytes(bytes, "render.png", "image/png");
                fileUrl = uploadResult.get("url");
                fileSize = bytes.length;
                log.info("Rendered print file uploaded via UploadService: {}", fileUrl);
            } catch (Exception ex) {
                log.warn("Upload via UploadService failed, fallback to local file storage. Reason: {}", ex.getMessage(), ex);
                File localFile = saveLocalPng(bytes);
                fileUrl = localFile.getAbsolutePath();
                fileSize = localFile.length();
                log.info("Rendered print file saved locally: {}", fileUrl);
            }

            return RenderResponse.builder()
                    .status("SUCCESS")
                    .fileUrl(fileUrl)
                    .fileSize(fileSize)
                    .widthPx(canvas.getWidth())
                    .heightPx(canvas.getHeight())
                    .dpi(dpi)
                    .renderTimeMs(System.currentTimeMillis() - startTime)
                    .renderedAt(Instant.now())
                    .build();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Print render failed", e);
            throw new AppException(ErrorCode.RENDER_FAILED, e.getMessage());
        } finally {
            log.debug("Cleaning up {} temp files after render", tempFiles.size());
            cleanupTempFiles(tempFiles);
        }
    }

    /** Composite garment image + design buffer = full mockup. */
    private BufferedImage compositeGarmentWithDesign(RenderPrintRequest request, BufferedImage designBuffer,
                                                     int designW, int designH, List<File> tempFiles) {
        BufferedImage garment = loadImageFromUrl(request.getGarmentImageUrl(), tempFiles);
        int gw = garment.getWidth();
        int gh = garment.getHeight();

        double leftR = request.getPrintAreaLeftRatio() != null ? request.getPrintAreaLeftRatio() : 0.25;
        double topR = request.getPrintAreaTopRatio() != null ? request.getPrintAreaTopRatio() : 0.125;
        double widthR = request.getPrintAreaWidthRatio() != null ? request.getPrintAreaWidthRatio() : 0.5;
        double heightR = request.getPrintAreaHeightRatio() != null ? request.getPrintAreaHeightRatio() : 0.75;

        int paLeft = (int) (leftR * gw);
        int paTop = (int) (topR * gh);
        int paW = (int) (widthR * gw);
        int paH = (int) (heightR * gh);

        BufferedImage result = new BufferedImage(gw, gh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = setupGraphics2D(result);
        g.drawImage(garment, 0, 0, null);
        g.drawImage(designBuffer, paLeft, paTop, paW, paH, null);
        g.dispose();
        return result;
    }

    private BufferedImage loadImageFromUrl(String url, List<File> tempFiles) {
        try {
            if (url.startsWith("data:")) {
                byte[] bytes = parseDataUrlToBytes(url);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                    return ImageIO.read(bais);
                }
            }
            File tmp = File.createTempFile("garment_", ".img");
            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            tempFiles.add(tmp);
            BufferedImage img = ImageIO.read(tmp);
            if (img == null) throw new AppException(ErrorCode.IMAGE_READ_FAILED, url);
            return img;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_READ_FAILED, url);
        }
    }

    /** Tạo canvas nền trong suốt cho file in (ARGB). */
    private BufferedImage createTransparentCanvas(int w, int h) {
        BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        try {
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, w, h);
            g.setComposite(AlphaComposite.SrcOver);
        } finally {
            g.dispose();
        }
        return canvas;
    }

    private Graphics2D setupGraphics2D(BufferedImage canvas) {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        return g2d;
    }

    private void renderPrintLayersMm(
            Graphics2D g2d,
            List<PrintDesignLayerRequest> layers,
            int dpi,
            List<File> tempFiles) {
        List<PrintDesignLayerRequest> sorted = new ArrayList<>(layers);
        sorted.sort(Comparator.comparing(l -> l.getZIndex() != null ? l.getZIndex() : 0));

        for (PrintDesignLayerRequest layer : sorted) {
            String type = layer.getType() == null ? "" : layer.getType().toLowerCase();
            log.debug("Rendering layer type={}, z_index={}, url/text_sample={}",
                    type,
                    layer.getZIndex(),
                    "image".equals(type) ? layer.getUrl() : (layer.getText() != null && layer.getText().length() > 20
                            ? layer.getText().substring(0, 20) + "..."
                            : layer.getText()));
            switch (type) {
                case "image" -> renderPrintImageLayerMm(g2d, layer, dpi, tempFiles);
                case "text" -> renderPrintTextLayerMm(g2d, layer, dpi);
                default -> throw new AppException(ErrorCode.INVALID_LAYER_TYPE, layer.getType());
            }
        }
    }

    private void renderPrintImageLayerMm(
            Graphics2D g2d,
            PrintDesignLayerRequest layer,
            int dpi,
            List<File> tempFiles) {
        if (layer.getUrl() == null || layer.getUrl().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "url");
        }
        try {
            String fetchUrl = toPngIfCloudinaryWebp(layer.getUrl());
            File imageFile;
            if (fetchUrl.startsWith("data:")) {
                byte[] bytes = parseDataUrlToBytes(fetchUrl);
                imageFile = File.createTempFile("layer_", ".png");
                Files.write(imageFile.toPath(), bytes);
            } else {
                imageFile = File.createTempFile("layer_", ".png");
                URI uri = URI.create(fetchUrl);
                try (InputStream in = uri.toURL().openStream()) {
                    Files.copy(in, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            tempFiles.add(imageFile);

            int wPx = UnitConverter.mmToPixels(layer.getWidthMm(), dpi);
            int hPx = UnitConverter.mmToPixels(layer.getHeightMm(), dpi);

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new AppException(ErrorCode.IMAGE_READ_FAILED, layer.getUrl());
            }

            int xPx = UnitConverter.mmToPixels(layer.getXMm(), dpi);
            int yPx = UnitConverter.mmToPixels(layer.getYMm(), dpi);
            double rotationRad = layer.getRotationDeg() != null ? UnitConverter.degreeToRadian(layer.getRotationDeg()) : 0;
            log.debug("Image layer: url={}, x_px={}, y_px={}, w_px={}, h_px={}, rotation_deg={}, opacity={}",
                    layer.getUrl(), xPx, yPx, wPx, hPx, layer.getRotationDeg(), layer.getOpacity());

            double sx = (double) wPx / image.getWidth();
            double sy = (double) hPx / image.getHeight();
            double cx = xPx + wPx / 2.0;
            double cy = yPx + hPx / 2.0;

            AffineTransform tx = new AffineTransform();
            tx.translate(cx, cy);
            tx.rotate(rotationRad);
            tx.translate(-wPx / 2.0, -hPx / 2.0);
            tx.scale(sx, sy);

            Composite old = g2d.getComposite();
            if (layer.getOpacity() != null) {
                g2d.setComposite(
                        AlphaComposite.getInstance(
                                AlphaComposite.SRC_OVER,
                                layer.getOpacity().floatValue()));
            }

            g2d.drawImage(image, tx, null);
            g2d.setComposite(old);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Render image layer failed for url={}: {}", layer.getUrl(), e.getMessage(), e);
            throw new AppException(ErrorCode.IMAGE_RENDER_FAILED, layer.getUrl());
        }
    }

    private void renderPrintTextLayerMm(
            Graphics2D g2d,
            PrintDesignLayerRequest layer,
            int dpi) {
        if (layer.getText() == null) return;

        int xPx = UnitConverter.mmToPixels(layer.getXMm(), dpi);
        int yPx = UnitConverter.mmToPixels(layer.getYMm(), dpi);
        double rotationRad = layer.getRotationDeg() != null ? UnitConverter.degreeToRadian(layer.getRotationDeg()) : 0;

        AffineTransform oldTx = null;
        if (Math.abs(rotationRad) > 1e-6) {
            oldTx = g2d.getTransform();
            g2d.translate(xPx, yPx);
            g2d.rotate(rotationRad);
            g2d.translate(-xPx, -yPx);
        }

        Font font = new Font(
                layer.getFontFamily() == null ? "Arial" : layer.getFontFamily(),
                Font.PLAIN,
                layer.getFontSize() == null ? 24 : layer.getFontSize());
        g2d.setFont(font);
        int[] rgb = UnitConverter.parseHexColor(
                layer.getFontColor() == null ? "#000000" : layer.getFontColor());
        g2d.setColor(new Color(rgb[0], rgb[1], rgb[2]));

        // Java drawString(x,y) uses y as BASELINE; our yPx is TOP of text box -> add ascent
        int ascent = g2d.getFontMetrics(font).getAscent();
        int drawY = yPx + ascent;

        log.debug("Text layer: text_sample=\"{}\", x_px={}, y_px={}, font_family={}, font_size={}, color={}",
                layer.getText().length() > 30 ? layer.getText().substring(0, 30) + "..." : layer.getText(),
                xPx, yPx, layer.getFontFamily(), layer.getFontSize(), layer.getFontColor());
        g2d.drawString(layer.getText(), xPx, drawY);

        if (oldTx != null) {
            g2d.setTransform(oldTx);
        }
    }


    /**
     * Java ImageIO không đọc được WebP. Với URL Cloudinary .webp, chèn f_png để lấy PNG.
     */
    private String toPngIfCloudinaryWebp(String url) {
        if (url == null || !url.contains("cloudinary.com")) return url;
        String lower = url.toLowerCase();
        if (lower.contains(".webp") || lower.contains("webp")) {
            String marker = "/image/upload/";
            int i = url.indexOf(marker);
            if (i >= 0) {
                int insertAt = i + marker.length();
                if (!url.substring(insertAt, Math.min(insertAt + 6, url.length())).startsWith("f_")) {
                    return url.substring(0, insertAt) + "f_png/" + url.substring(insertAt);
                }
            }
        }
        return url;
    }

    /** Parse data:image/...;base64,XXX URL to raw bytes. */
    private byte[] parseDataUrlToBytes(String dataUrl) {
        int comma = dataUrl.indexOf(',');
        if (comma < 0 || !dataUrl.toLowerCase().contains("base64")) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid data URL");
        }
        return Base64.getDecoder().decode(dataUrl.substring(comma + 1));
    }

    private void cleanupTempFiles(List<File> files) {
        for (File f : files) {
            try {
                Files.deleteIfExists(f.toPath());
            } catch (Exception e) {
                log.warn("Could not delete temp file: {}", f.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Lưu file PNG ra đĩa local khi không thể upload qua service ngoài.
     */
    private File saveLocalPng(byte[] bytes) {
        try {
            File dir = new File(LOCAL_RENDER_DIR);
            Files.createDirectories(dir.toPath());

            File file = new File(dir, "render_" + UUID.randomUUID() + ".png");
            Files.write(file.toPath(), bytes);
            return file;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_IMAGE_FAILED, e.getMessage());
        }
    }

}
