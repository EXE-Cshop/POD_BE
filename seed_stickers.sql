-- =====================================================================
-- C-SHOP | Seed Data - Stickers
-- Database: pod_db (PostgreSQL)
-- =====================================================================
-- HƯỚNG DẪN:
--   1. Mở pgAdmin → chọn database "pod_db"
--   2. Tools → Query Tool → Paste nội dung file này → Run (F5)
--   3. Đảm bảo backend đã chạy ít nhất 1 lần để Hibernate tạo bảng stickers
-- =====================================================================

-- Xóa dữ liệu cũ (tùy chọn - bỏ comment nếu muốn chạy lại sạch)
-- DELETE FROM stickers;

-- label: tên hiển thị (nullable). Với upload mới, label lấy từ tên file.
INSERT INTO stickers (link, label, created_date, created_by)
VALUES
    ('https://res.cloudinary.com/di5j3h6wi/image/upload/v1772702410/nhan-dan-shin-32_gph9dv.webp', 'Shin 32', NOW(), 'seed-script'),
    ('https://res.cloudinary.com/di5j3h6wi/image/upload/v1772702400/nhan-dan-shin-28_j8654o.webp', 'Shin 28', NOW(), 'seed-script'),
    ('https://res.cloudinary.com/di5j3h6wi/image/upload/v1772702393/nhan-dan-shin-18_rdloie.webp', 'Shin 18', NOW(), 'seed-script'),
    ('https://res.cloudinary.com/di5j3h6wi/image/upload/v1772701332/452104527_474641188623178_4279285114250487564_n-removebg-preview_vo3mmd.png', 'Preview', NOW(), 'seed-script');
