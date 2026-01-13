-- 患者データと診察券番号をデバッグするSQL

USE clinic_booking_db;

-- 1. 患者テーブルの構造を確認
DESCRIBE patients;

-- 2. すべての患者データを確認
SELECT 
    patient_id,
    patient_number,
    name,
    phone,
    created_at
FROM patients
ORDER BY patient_id;

-- 3. 診察券番号がNULLまたは空の患者を確認
SELECT 
    patient_id,
    patient_number,
    name,
    phone
FROM patients
WHERE patient_number IS NULL OR patient_number = '';

-- 4. 最大の診察券番号を確認
SELECT MAX(patient_number) as max_patient_number FROM patients;

-- 5. 特定の診察券番号で検索（例: 0000181）
SELECT 
    patient_id,
    patient_number,
    name,
    phone
FROM patients
WHERE patient_number = '0000181';

-- 6. 診察券番号の形式を確認（先頭・末尾の空白など）
SELECT 
    patient_id,
    patient_number,
    LENGTH(patient_number) as length,
    CHAR_LENGTH(patient_number) as char_length,
    CONCAT('"', patient_number, '"') as with_quotes
FROM patients
ORDER BY patient_id;
