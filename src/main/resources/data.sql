-- ============================================
-- SEED DATA: PRODUCTS, VARIANTS, PRINT AREAS
-- ============================================
-- 5 sản phẩm mẫu: Áo thun, Cốc, Túi tote, Áo hoodie, Mũ lưỡi trai
-- ============================================
-- Lưu ý: Schema thực tế sử dụng created_at, updated_at (không có created_by, modified_by)
-- print_areas và product_variants không có timestamp columns trong schema
-- ============================================

-- Xóa data cũ nếu có (để tránh duplicate)
DELETE FROM product_variants;
DELETE FROM print_areas;
DELETE FROM base_products;

-- Reset sequence (PostgreSQL)
ALTER SEQUENCE base_products_id_seq RESTART WITH 1;
ALTER SEQUENCE product_variants_id_seq RESTART WITH 1;
ALTER SEQUENCE print_areas_id_seq RESTART WITH 1;

-- ============================================
-- 1. ÁO THUN (T-SHIRT)
-- ============================================
INSERT INTO base_products (name, description, base_price, material, active, created_at, updated_at)
VALUES ('Áo Thun Cổ Tròn', 'Áo thun cotton 100%, thoáng mát, dễ giặt', 250000.00, 'Cotton 100%', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
RETURNING id;

-- Lưu ID vào biến tạm (PostgreSQL không hỗ trợ biến, nên dùng cách khác)
-- Sử dụng CTE hoặc tách thành nhiều statements

-- Print Areas cho Áo Thun
-- name là enum (ví dụ: FRONT_CENTER/BACK_CENTER) theo PrintAreaName
INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'FRONT_CENTER', 300.00, 350.00, 15.00, 25.00, 50.00, 60.00, NULL
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'BACK_CENTER', 300.00, 350.00, 15.00, 25.00, 50.00, 60.00, NULL
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

-- Variants cho Áo Thun
INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'S', 'TSHIRT-BLACK-S', 100,
       'https://example.com/images/tshirt-black-front.jpg', 'https://example.com/images/tshirt-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'M', 'TSHIRT-BLACK-M', 150,
       'https://example.com/images/tshirt-black-front.jpg', 'https://example.com/images/tshirt-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'L', 'TSHIRT-BLACK-L', 120,
       'https://example.com/images/tshirt-black-front.jpg', 'https://example.com/images/tshirt-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'S', 'TSHIRT-WHITE-S', 80,
       'https://example.com/images/tshirt-white-front.jpg', 'https://example.com/images/tshirt-white-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'M', 'TSHIRT-WHITE-M', 100,
       'https://example.com/images/tshirt-white-front.jpg', 'https://example.com/images/tshirt-white-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'L', 'TSHIRT-WHITE-L', 90,
       'https://example.com/images/tshirt-white-front.jpg', 'https://example.com/images/tshirt-white-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Navy', '#001F3F', 'M', 'TSHIRT-NAVY-M', 60,
       'https://example.com/images/tshirt-navy-front.jpg', 'https://example.com/images/tshirt-navy-back.jpg',
       20000.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Navy', '#001F3F', 'L', 'TSHIRT-NAVY-L', 70,
       'https://example.com/images/tshirt-navy-front.jpg', 'https://example.com/images/tshirt-navy-back.jpg',
       20000.00, true
FROM base_products
WHERE name = 'Áo Thun Cổ Tròn'
LIMIT 1;

-- ============================================
-- 2. CỐC (MUG)
-- ============================================
INSERT INTO base_products (name, description, base_price, material, active, created_at, updated_at)
VALUES ('Cốc Sứ In Hình', 'Cốc sứ cao cấp, dung tích 350ml, có thể in hình 360 độ', 150000.00, 'Sứ Ceramic', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Print Areas cho Cốc
INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'FRONT_CENTER', 280.00, 100.00, 0.00, 0.00, 100.00, 100.00, NULL
FROM base_products
WHERE name = 'Cốc Sứ In Hình'
LIMIT 1;

-- Variants cho Cốc (One Size)
INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'One Size', 'MUG-WHITE-OS', 200,
       'https://example.com/images/mug-white-front.jpg', NULL,
       0.00, true
FROM base_products
WHERE name = 'Cốc Sứ In Hình'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'One Size', 'MUG-BLACK-OS', 150,
       'https://example.com/images/mug-black-front.jpg', NULL,
       30000.00, true
FROM base_products
WHERE name = 'Cốc Sứ In Hình'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Dương', '#0066CC', 'One Size', 'MUG-BLUE-OS', 100,
       'https://example.com/images/mug-blue-front.jpg', NULL,
       25000.00, true
FROM base_products
WHERE name = 'Cốc Sứ In Hình'
LIMIT 1;

-- ============================================
-- 3. TÚI TOTE (TOTE BAG)
-- ============================================
INSERT INTO base_products (name, description, base_price, material, active, created_at, updated_at)
VALUES ('Túi Tote Canvas', 'Túi tote canvas bền chắc, kích thước 40x40cm, có thể in mặt trước và sau', 180000.00, 'Canvas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Print Areas cho Túi Tote
INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'FRONT_CENTER', 350.00, 400.00, 10.00, 10.00, 80.00, 80.00, NULL
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'BACK_CENTER', 350.00, 400.00, 10.00, 10.00, 80.00, 80.00, NULL
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

-- Variants cho Túi Tote (One Size)
INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'One Size', 'TOTE-WHITE-OS', 80,
       'https://example.com/images/tote-white-front.jpg', 'https://example.com/images/tote-white-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'One Size', 'TOTE-BLACK-OS', 90,
       'https://example.com/images/tote-black-front.jpg', 'https://example.com/images/tote-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xám', '#808080', 'One Size', 'TOTE-GRAY-OS', 70,
       'https://example.com/images/tote-gray-front.jpg', 'https://example.com/images/tote-gray-back.jpg',
       20000.00, true
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Rêu', '#556B2F', 'One Size', 'TOTE-OLIVE-OS', 50,
       'https://example.com/images/tote-olive-front.jpg', 'https://example.com/images/tote-olive-back.jpg',
       30000.00, true
FROM base_products
WHERE name = 'Túi Tote Canvas'
LIMIT 1;

-- ============================================
-- 4. ÁO HOODIE
-- ============================================
INSERT INTO base_products (name, description, base_price, material, active, created_at, updated_at)
VALUES ('Áo Hoodie Có Mũ', 'Áo hoodie dày dặn, có mũ trùm đầu, phù hợp mùa đông', 450000.00, 'Cotton 80% + Polyester 20%', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Print Areas cho Áo Hoodie
INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'FRONT_CENTER', 350.00, 400.00, 15.00, 25.00, 55.00, 60.00, NULL
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'BACK_CENTER', 350.00, 450.00, 12.00, 25.00, 55.00, 65.00, NULL
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'LEFT_SLEEVE', 100.00, 500.00, 10.00, 5.00, 20.00, 70.00, NULL
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'RIGHT_SLEEVE', 100.00, 500.00, 10.00, 75.00, 20.00, 70.00, NULL
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

-- Variants cho Áo Hoodie
INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'M', 'HOODIE-BLACK-M', 40,
       'https://example.com/images/hoodie-black-front.jpg', 'https://example.com/images/hoodie-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'L', 'HOODIE-BLACK-L', 50,
       'https://example.com/images/hoodie-black-front.jpg', 'https://example.com/images/hoodie-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'XL', 'HOODIE-BLACK-XL', 35,
       'https://example.com/images/hoodie-black-front.jpg', 'https://example.com/images/hoodie-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xám', '#808080', 'M', 'HOODIE-GRAY-M', 30,
       'https://example.com/images/hoodie-gray-front.jpg', 'https://example.com/images/hoodie-gray-back.jpg',
       50000.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xám', '#808080', 'L', 'HOODIE-GRAY-L', 40,
       'https://example.com/images/hoodie-gray-front.jpg', 'https://example.com/images/hoodie-gray-back.jpg',
       50000.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Navy', '#001F3F', 'L', 'HOODIE-NAVY-L', 25,
       'https://example.com/images/hoodie-navy-front.jpg', 'https://example.com/images/hoodie-navy-back.jpg',
       70000.00, true
FROM base_products
WHERE name = 'Áo Hoodie Có Mũ'
LIMIT 1;

-- ============================================
-- 5. MŨ LƯỠI TRAI (CAP)
-- ============================================
INSERT INTO base_products (name, description, base_price, material, active, created_at, updated_at)
VALUES ('Mũ Lưỡi Trai', 'Mũ lưỡi trai thể thao, có thể điều chỉnh size, in được mặt trước và mặt sau', 120000.00, 'Polyester + Cotton', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Print Areas cho Mũ Lưỡi Trai
INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'FRONT_CENTER', 200.00, 80.00, 15.00, 25.00, 50.00, 30.00, NULL
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

INSERT INTO print_areas (base_product_id, name, width_mm, height_mm, top_offset_percent, left_offset_percent, width_percent, height_percent, mask_image_url)
SELECT id, 'BACK_CENTER', 200.00, 80.00, 15.00, 25.00, 50.00, 30.00, NULL
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

-- Variants cho Mũ Lưỡi Trai (One Size)
INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đen', '#000000', 'One Size', 'CAP-BLACK-OS', 120,
       'https://example.com/images/cap-black-front.jpg', 'https://example.com/images/cap-black-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Trắng', '#FFFFFF', 'One Size', 'CAP-WHITE-OS', 100,
       'https://example.com/images/cap-white-front.jpg', 'https://example.com/images/cap-white-back.jpg',
       0.00, true
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Navy', '#001F3F', 'One Size', 'CAP-NAVY-OS', 80,
       'https://example.com/images/cap-navy-front.jpg', 'https://example.com/images/cap-navy-back.jpg',
       20000.00, true
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Đỏ', '#DC143C', 'One Size', 'CAP-RED-OS', 60,
       'https://example.com/images/cap-red-front.jpg', 'https://example.com/images/cap-red-back.jpg',
       25000.00, true
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

INSERT INTO product_variants (base_product_id, color_name, color_hex, size, sku, stock_quantity, front_image_url, back_image_url, price_adjustment, active)
SELECT id, 'Xanh Lá', '#228B22', 'One Size', 'CAP-GREEN-OS', 50,
       'https://example.com/images/cap-green-front.jpg', 'https://example.com/images/cap-green-back.jpg',
       25000.00, true
FROM base_products
WHERE name = 'Mũ Lưỡi Trai'
LIMIT 1;

-- ============================================
-- SUMMARY
-- ============================================
-- Tổng cộng:
-- - 5 Base Products
-- - 15 Print Areas (2-4 areas mỗi sản phẩm)
-- - 30 Product Variants (6-8 variants mỗi sản phẩm)
-- ============================================
