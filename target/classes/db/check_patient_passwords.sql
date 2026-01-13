-- 患者データとパスワード形式を確認するSQL
USE clinic_booking_db;

-- すべての患者データとパスワード形式を表示
SELECT 
    patient_id,
    patient_number,
    name,
    phone,
    LENGTH(password) as password_length,
    SUBSTRING(password, 1, 30) as password_preview,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2b$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2y$%' THEN 'BCrypt形式'
        ELSE 'その他の形式（更新が必要）'
    END as password_format
FROM patients
ORDER BY patient_id DESC;

-- BCrypt形式でないパスワードを持つ患者を確認
SELECT 
    patient_id,
    patient_number,
    name,
    phone,
    SUBSTRING(password, 1, 30) as password_preview
FROM patients
WHERE password NOT LIKE '$2a$%' 
   AND password NOT LIKE '$2b$%' 
   AND password NOT LIKE '$2y$%'
ORDER BY patient_id DESC;
