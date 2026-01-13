-- 既存のデータベースに診察券番号カラムを追加する場合のマイグレーションSQL
-- 注意: 既存のデータがある場合は、このスクリプトを実行する前にバックアップを取ってください

USE clinic_booking_db;

-- 診察券番号カラムを追加
ALTER TABLE patients 
ADD COLUMN patient_number VARCHAR(20) NULL AFTER patient_id;

-- 既存の患者データに診察券番号を割り当て（最初の患者から0000181を開始）
SET @row_number = 180;
UPDATE patients 
SET patient_number = LPAD(@row_number := @row_number + 1, 7, '0')
WHERE patient_number IS NULL
ORDER BY patient_id;

-- ユニーク制約を追加
ALTER TABLE patients 
ADD UNIQUE KEY uk_patient_number (patient_number);

-- NOT NULL制約を追加
ALTER TABLE patients 
MODIFY COLUMN patient_number VARCHAR(20) NOT NULL;
