-- patientsテーブルのcard_numberカラムを修正するSQL
USE clinic_booking_db;

-- 現在のテーブル構造を確認
DESCRIBE patients;

-- オプション1: card_numberカラムにデフォルト値を設定（NULL許可に変更）
ALTER TABLE patients 
MODIFY COLUMN card_number VARCHAR(20) NULL;

-- または、オプション2: card_numberカラムを削除（不要な場合）
-- ALTER TABLE patients DROP COLUMN card_number;

-- 確認
DESCRIBE patients;
