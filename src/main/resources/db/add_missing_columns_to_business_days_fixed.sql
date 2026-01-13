-- business_daysテーブルに不足しているカラムを追加するSQL（修正版）
USE clinic_booking_db;

-- 現在のテーブル構造を確認
DESCRIBE business_days;

-- 1. time_slotsカラムを追加（TEXT型なのでデフォルト値は設定できない）
ALTER TABLE business_days 
ADD COLUMN time_slots TEXT NOT NULL AFTER is_accepting;

-- 2. 既存のデータにデフォルト値を設定
UPDATE business_days 
SET time_slots = '09:00,10:00,11:00,14:00,15:00,16:00'
WHERE time_slots IS NULL OR time_slots = '';

-- 3. created_atカラムを追加
ALTER TABLE business_days 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER time_slots;

-- 4. 確認
DESCRIBE business_days;

-- 5. データ確認
SELECT * FROM business_days;
