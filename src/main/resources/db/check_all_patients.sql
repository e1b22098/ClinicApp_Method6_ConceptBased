-- すべての患者データを確認するSQL
USE clinic_booking_db;

-- すべての患者データを表示（最新から順）
SELECT 
    patient_id,
    patient_number,
    name,
    birth_date,
    phone,
    LENGTH(password) as password_length,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt形式'
        ELSE 'その他の形式'
    END as password_format
FROM patients
ORDER BY patient_id DESC;

-- 診察券番号がNULLの患者を確認
SELECT 
    patient_id,
    patient_number,
    name,
    phone
FROM patients
WHERE patient_number IS NULL;

-- 最新の患者の詳細情報
SELECT 
    patient_id,
    patient_number,
    name,
    birth_date,
    phone,
    SUBSTRING(password, 1, 20) as password_preview
FROM patients
ORDER BY patient_id DESC
LIMIT 5;
