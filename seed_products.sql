-- =====================================================================
-- C-SHOP | Seed Data - Sản phẩm demo CHANGNAS T-Shirt
-- Database: pod_db (PostgreSQL)
-- Ngày tạo: 2026-03-04
-- =====================================================================
-- HƯỚNG DẪN:
--   1. Mở pgAdmin → chọn database "pod_db"
--   2. Tools → Query Tool → Paste toàn bộ nội dung file này → Run (F5)
--   3. Đảm bảo backend đã chạy ít nhất 1 lần để Hibernate tạo bảng
-- =====================================================================

-- =====================================================================
-- BƯỚC 0: Fix kiểu cột (Hibernate mặc định varchar(255), URL dài hơn)
-- =====================================================================
ALTER TABLE base_products ALTER COLUMN image_url TYPE TEXT;
ALTER TABLE base_products ALTER COLUMN description TYPE TEXT;
ALTER TABLE base_products ALTER COLUMN material TYPE TEXT;
ALTER TABLE product_variants ALTER COLUMN front_image_url TYPE TEXT;
ALTER TABLE product_variants ALTER COLUMN back_image_url TYPE TEXT;

-- =====================================================================
-- BƯỚC 1: Xóa dữ liệu demo cũ (nếu có) — chạy lại an toàn
-- =====================================================================
DELETE FROM print_areas WHERE base_product_id IN (
    SELECT id FROM base_products WHERE name LIKE 'CHANGNAS%' OR name LIKE 'Changnas%'
);
DELETE FROM product_variants WHERE base_product_id IN (
    SELECT id FROM base_products WHERE name LIKE 'CHANGNAS%' OR name LIKE 'Changnas%'
);
DELETE FROM base_products WHERE name LIKE 'CHANGNAS%' OR name LIKE 'Changnas%';

-- =====================================================================
-- BƯỚC 2: Thêm 4 Base Products
-- =====================================================================
INSERT INTO base_products (name, description, base_price, material, print_technology, image_url, active, created_date, modified_date, created_by, modified_by)
VALUES
-- Product 1: Dripping Houses - Black
(
    'CHANGNAS Dripping Houses Tee - Black Edition',
    'Áo thun oversize phiên bản đen với thiết kế CHANGNAS Dripping Houses độc đáo. Chất liệu Cotton 4 chiều cao cấp 250gsm, phom dáng rộng thoải mái. Hình in kỹ thuật DTG sắc nét, bền màu sau nhiều lần giặt.',
    350000, '100% Cotton 4 chiều - 250gsm', 'DTG',
    'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
    true, NOW(), NOW(), 'admin', 'admin'
),
-- Product 2: Dripping Houses - White
(
    'CHANGNAS Dripping Houses Tee - White Edition',
    'Áo thun oversize phiên bản trắng với thiết kế CHANGNAS Dripping Houses nổi bật. Chất liệu Cotton 4 chiều cao cấp 250gsm, phom dáng rộng thoải mái. Hình in kỹ thuật DTG chi tiết sắc nét trên nền trắng.',
    350000, '100% Cotton 4 chiều - 250gsm', 'DTG',
    'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
    true, NOW(), NOW(), 'admin', 'admin'
),
-- Product 3: Gothic Logo - Black
(
    'Changnas Gothic Logo Tee - Black',
    'Áo thun Changnas phiên bản đen với logo Gothic cổ điển in chính giữa ngực. Thiết kế tối giản nhưng sang trọng, phom oversize thoải mái. Chất liệu Cotton 4 chiều 250gsm, in kỹ thuật DTG bền màu.',
    299000, '100% Cotton 4 chiều - 250gsm', 'DTG',
    'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
    true, NOW(), NOW(), 'admin', 'admin'
),
-- Product 4: Gothic Logo - White
(
    'Changnas Gothic Logo Tee - White',
    'Áo thun Changnas phiên bản trắng với logo Gothic tinh tế in chính giữa ngực. Thiết kế clean và hiện đại, phom oversize cá tính. Chất liệu Cotton 4 chiều 250gsm, in kỹ thuật DTG sắc nét.',
    299000, '100% Cotton 4 chiều - 250gsm', 'DTG',
    'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
    true, NOW(), NOW(), 'admin', 'admin'
);

-- =====================================================================
-- BƯỚC 3: Thêm Product Variants (4 sizes x 4 products = 16 variants)
-- =====================================================================

-- ── Product 1: Dripping Houses - Black (Đen) ──
INSERT INTO product_variants (color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active, base_product_id, created_date, created_by)
VALUES
('Đen', '#000000', 'S',  'CHANGNAS-BLK-S',  50,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'M',  'CHANGNAS-BLK-M',  100,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'L',  'CHANGNAS-BLK-L',  80,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'XL', 'CHANGNAS-BLK-XL', 60,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615163/MauAoDen_omn3il.jpg?_s=public-apps',
 10000, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin');

-- ── Product 2: Dripping Houses - White (Trắng) ──
INSERT INTO product_variants (color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active, base_product_id, created_date, created_by)
VALUES
('Trắng', '#FFFFFF', 'S',  'CHANGNAS-WHT-S',  45,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'M',  'CHANGNAS-WHT-M',  90,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'L',  'CHANGNAS-WHT-L',  75,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'XL', 'CHANGNAS-WHT-XL', 55,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772615183/MauAoTrang_oiuein.jpg?_s=public-apps',
 10000, true, (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin');

-- ── Product 3: Gothic Logo - Black (Đen) ──
INSERT INTO product_variants (color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active, base_product_id, created_date, created_by)
VALUES
('Đen', '#000000', 'S',  'GOTHIC-BLK-S',  55,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'M',  'GOTHIC-BLK-M',  110,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'L',  'GOTHIC-BLK-L',  85,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin'),
('Đen', '#000000', 'XL', 'GOTHIC-BLK-XL', 65,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772617682/b2a8c03b0b0761bb2bf0e4e6e7d5774b_nrk6ub.jpg?_s=public-apps',
 10000, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin');

-- ── Product 4: Gothic Logo - White (Trắng) ──
INSERT INTO product_variants (color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active, base_product_id, created_date, created_by)
VALUES
('Trắng', '#FFFFFF', 'S',  'GOTHIC-WHT-S',  50,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'M',  'GOTHIC-WHT-M',  95,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'L',  'GOTHIC-WHT-L',  70,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 0, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin'),
('Trắng', '#FFFFFF', 'XL', 'GOTHIC-WHT-XL', 50,
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 'https://res.cloudinary.com/di5j3h6wi/image/upload/fl_preserve_transparency/v1772618145/MauAoTrang2_hd2m4x.jpg?_s=public-apps',
 10000, true, (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin');

-- =====================================================================
-- BƯỚC 4: Thêm Print Areas (FRONT + BACK cho mỗi sản phẩm = 8 areas)
-- =====================================================================
INSERT INTO print_areas (name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url, base_product_id, created_date, created_by)
VALUES
-- Dripping Houses - Black
('FRONT_CENTER', 300, 400, 18.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin'),
('BACK_CENTER',  300, 400, 15.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - Black Edition' LIMIT 1), NOW(), 'admin'),
-- Dripping Houses - White
('FRONT_CENTER', 300, 400, 18.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin'),
('BACK_CENTER',  300, 400, 15.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'CHANGNAS Dripping Houses Tee - White Edition' LIMIT 1), NOW(), 'admin'),
-- Gothic Logo - Black
('FRONT_CENTER', 300, 400, 18.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin'),
('BACK_CENTER',  300, 400, 15.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - Black' LIMIT 1), NOW(), 'admin'),
-- Gothic Logo - White
('FRONT_CENTER', 300, 400, 18.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin'),
('BACK_CENTER',  300, 400, 15.0, 20.0, 60.0, 55.0, NULL,
 (SELECT id FROM base_products WHERE name = 'Changnas Gothic Logo Tee - White' LIMIT 1), NOW(), 'admin');

-- =====================================================================
-- KIỂM TRA KẾT QUẢ
-- =====================================================================
SELECT '=== BASE PRODUCTS ===' AS info;
SELECT id, name, base_price, active FROM base_products WHERE name LIKE 'CHANGNAS%' OR name LIKE 'Changnas%' ORDER BY id;

SELECT '=== PRODUCT VARIANTS ===' AS info;
SELECT pv.id, pv.sku, pv.color_name, pv.size, pv.stock_quantity, pv.price_adjustment, bp.name AS product
FROM product_variants pv
JOIN base_products bp ON pv.base_product_id = bp.id
WHERE bp.name LIKE 'CHANGNAS%' OR bp.name LIKE 'Changnas%'
ORDER BY bp.id, pv.color_name, pv.size;

SELECT '=== PRINT AREAS ===' AS info;
SELECT pa.id, pa.name AS area, bp.name AS product
FROM print_areas pa
JOIN base_products bp ON pa.base_product_id = bp.id
WHERE bp.name LIKE 'CHANGNAS%' OR bp.name LIKE 'Changnas%'
ORDER BY bp.id, pa.name;
