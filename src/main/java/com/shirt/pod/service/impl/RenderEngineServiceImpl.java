package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.model.dto.request.DesignLayerRequest;
import com.shirt.pod.model.dto.request.RenderRequest;
import com.shirt.pod.model.dto.response.RenderResponse;
import com.shirt.pod.service.RenderEngineService;
import com.shirt.pod.utils.UnitConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenderEngineServiceImpl implements RenderEngineService {

    private static final String RENDER_DIR = "tmp/renders";
    private static final int DEFAULT_DPI = 300;

    @Override
    public RenderResponse renderDesign(RenderRequest request) {
        long startTime = System.currentTimeMillis();
        File outputFile;
        List<File> tempFiles = new ArrayList<>();

        try {
            if (request.getLayers() == null || request.getLayers().isEmpty()) {
                throw new AppException(ErrorCode.INVALID_INPUT, "layers");
            }

            int dpi = request.getDpi() != null ? request.getDpi() : DEFAULT_DPI;

            BigDecimal widthMm = request.getWidthMm();
            BigDecimal heightMm = request.getHeightMm();

            int canvasWidth = UnitConverter.mmToPixels(widthMm, dpi);
            int canvasHeight = UnitConverter.mmToPixels(heightMm, dpi);

            BufferedImage canvas = createCanvas(canvasWidth, canvasHeight);

            Graphics2D g2d = setupGraphics2D(canvas);

            String bgImageUrl = request.getBackgroundImageUrl();
            if (bgImageUrl != null && !bgImageUrl.isBlank()) {
                drawBackgroundImage(g2d, bgImageUrl, canvasWidth, canvasHeight, tempFiles);
            }

            renderLayers(g2d, request.getLayers(), tempFiles);

            g2d.dispose();

            outputFile = saveLocalPng(canvas);

            return RenderResponse.builder()
                    .status("SUCCESS")
                    .fileUrl(outputFile.getAbsolutePath())
                    .fileSize(outputFile.length())
                    .widthPx(canvasWidth)
                    .heightPx(canvasHeight)
                    .dpi(dpi)
                    .renderTimeMs(System.currentTimeMillis() - startTime)
                    .renderedAt(Instant.now())
                    .build();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Render failed", e);
            throw new AppException(ErrorCode.RENDER_FAILED, e.getMessage());
        } finally {
            cleanupTempFiles(tempFiles);
        }
    }

    /** Tạo canvas với nền trắng (dùng khi không có background_image_url hoặc làm lớp dưới ảnh nền). */
    private BufferedImage createCanvas(int w, int h) {
        BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
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

    /**
     * Tải ảnh áo từ URL và vẽ phủ toàn bộ canvas (nền dưới sticker/chữ).
     * File tạm được thêm vào tempFiles để xóa sau khi render xong.
     */
    private void drawBackgroundImage(
            Graphics2D g2d,
            String url,
            int canvasWidth,
            int canvasHeight,
            List<File> tempFiles) {
        try {
            File imageFile = File.createTempFile("bg_", ".img");
            URI uri = URI.create(url);
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            tempFiles.add(imageFile);

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new AppException(ErrorCode.IMAGE_READ_FAILED, url);
            }

            g2d.drawImage(image, 0, 0, canvasWidth, canvasHeight, null);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_RENDER_FAILED, url);
        }
    }

    private void renderLayers(
            Graphics2D g2d,
            List<DesignLayerRequest> layers,
            List<File> tempFiles) {
        List<DesignLayerRequest> sorted = new ArrayList<>(layers);
        sorted.sort(Comparator.comparing(l -> l.getZIndex() != null ? l.getZIndex() : 0));

        for (DesignLayerRequest layer : sorted) {
            switch (layer.getType().toLowerCase()) {
                case "image" -> renderImageLayer(g2d, layer, tempFiles);
                case "text" -> renderTextLayer(g2d, layer);
                default -> throw new AppException(ErrorCode.INVALID_LAYER_TYPE, layer.getType());
            }
        }
    }

    private void renderImageLayer(
            Graphics2D g2d,
            DesignLayerRequest layer,
            List<File> tempFiles) {
        try {
            File imageFile = File.createTempFile("layer_", ".img");
            URI uri = URI.create(layer.getUrl());
            try (InputStream in = uri.toURL().openStream()) {
                Files.copy(in, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            tempFiles.add(imageFile);

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                throw new AppException(ErrorCode.IMAGE_READ_FAILED, layer.getUrl());
            }

            AffineTransform tx = new AffineTransform();
            tx.translate(layer.getX().doubleValue(), layer.getY().doubleValue());

            double sx = layer.getWidth().doubleValue() / image.getWidth();
            double sy = layer.getHeight().doubleValue() / image.getHeight();
            tx.scale(sx, sy);

            if (layer.getRotation() != null) {
                tx.rotate(
                        UnitConverter.degreeToRadian(layer.getRotation()),
                        layer.getWidth().doubleValue() / 2,
                        layer.getHeight().doubleValue() / 2);
            }

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
            throw new AppException(ErrorCode.IMAGE_RENDER_FAILED, layer.getUrl());
        }
    }

    private void renderTextLayer(Graphics2D g2d, DesignLayerRequest layer) {
        if (layer.getText() == null)
            return;

        g2d.setFont(new Font(
                layer.getFontFamily() == null ? "Arial" : layer.getFontFamily(),
                Font.PLAIN,
                layer.getFontSize() == null ? 24 : layer.getFontSize()));

        int[] rgb = UnitConverter.parseHexColor(
                layer.getFontColor() == null ? "#000000" : layer.getFontColor());
        g2d.setColor(new Color(rgb[0], rgb[1], rgb[2]));

        g2d.drawString(
                layer.getText(),
                layer.getX().intValue(),
                layer.getY().intValue());
    }

    private File saveLocalPng(BufferedImage canvas) {
        try {
            File dir = new File(RENDER_DIR);
            Files.createDirectories(dir.toPath());

            File file = new File(dir, "render_" + UUID.randomUUID() + ".png");
            ImageIO.write(canvas, "PNG", file);
            return file;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_IMAGE_FAILED, e.getMessage());
        }
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
}
