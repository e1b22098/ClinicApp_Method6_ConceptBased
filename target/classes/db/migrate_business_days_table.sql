-- business_daysテーブルをスキーマに合わせて修正するSQL
USE clinic_booking_db;

-- 現在のテーブル構造を確認
DESCRIBE business_days;

-- 1. business_day_idカラムを追加（まずはPRIMARY KEYなしで追加）
ALTER TABLE business_days 
ADD COLUMN business_day_id INT AUTO_INCREMENT UNIQUE FIRST;

-- 2. 既存のデータにbusiness_day_idを設定（連番を振る）
SET @row_number = 0;
UPDATE business_days 
SET business_day_id = (@row_number := @row_number + 1)
WHERE business_day_id IS NULL;

-- 3. PRIMARY KEYをbusiness_dateからbusiness_day_idに変更
-- まず、既存のPRIMARY KEYを削除
ALTER TABLE business_days 
DROP PRIMARY KEY;

-- business_day_idをPRIMARY KEYに設定
ALTER TABLE business_days 
MODIFY COLUMN business_day_id INT AUTO_INCREMENT PRIMARY KEY;

-- business_dateにUNIQUE制約を追加
ALTER TABLE business_days 
ADD UNIQUE KEY uk_business_date (business_date);

-- 4. is_acceptingカラムを追加
ALTER TABLE business_days 
ADD COLUMN is_accepting BOOLEAN NOT NULL DEFAULT TRUE AFTER business_date;

-- 既存のis_openデータをis_acceptingにコピー
UPDATE business_days 
SET is_accepting = is_open;

-- 5. time_slotsカラムを追加
ALTER TABLE business_days 
ADD COLUMN time_slots TEXT NOT NULL DEFAULT '09:00,10:00,11:00,14:00,15:00,16:00' AFTER is_accepting;

-- 6. created_atカラムを追加
ALTER TABLE business_days 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER time_slots;

-- 7. 古いis_openカラムを削除（オプション - 必要に応じてコメントアウトを解除）
-- ALTER TABLE business_days DROP COLUMN is_open;

-- 8. 確認
DESCRIBE business_days;

-- 9. データ確認
SELECT * FROM business_days;
