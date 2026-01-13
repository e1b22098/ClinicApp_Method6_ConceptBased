-- 患者データと診察券番号を確認するSQL
-- このSQLを実行して、診察券番号が正しく保存されているか確認してください

USE clinic_booking_db;

-- 患者テーブルの構造を確認
DESCRIBE patients;

-- 患者データと診察券番号を確認（カラム名を確認してから実行）
-- まず、すべてのカラムを確認
SELECT * FROM patients ORDER BY patient_id LIMIT 5;

-- 診察券番号が存在する場合
SELECT 
    patient_id,
    patient_number,
    name,
    created_at
FROM patients
ORDER BY patient_id;

-- 診察券番号がNULLの患者を確認
SELECT 
    patient_id,
    patient_number,
    name
FROM patients
WHERE patient_number IS NULL;

-- 最大の診察券番号を確認
SELECT MAX(patient_number) as max_patient_number FROM patients;
