-- business_daysテーブルにbusiness_day_idカラムを追加するSQL
USE clinic_booking_db;

-- テーブル構造を確認
DESCRIBE business_days;

-- business_day_idカラムが存在しない場合は追加
-- 注意: 既存のデータがある場合は、まずテーブル構造を確認してから実行してください

-- 方法1: 既存のテーブルにbusiness_day_idを追加（既存のデータがある場合）
-- ALTER TABLE business_days 
-- ADD COLUMN business_day_id INT AUTO_INCREMENT PRIMARY KEY FIRST;

-- 方法2: テーブルを再作成（既存のデータを失う可能性があるため注意）
-- DROP TABLE IF EXISTS business_days;
-- CREATE TABLE business_days (
--     business_day_id INT AUTO_INCREMENT PRIMARY KEY,
--     business_date DATE NOT NULL UNIQUE,
--     is_accepting BOOLEAN NOT NULL DEFAULT TRUE,
--     time_slots TEXT NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
