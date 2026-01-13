-- 既存の患者データのパスワードを "test1234" に更新するSQL
-- パスワード "test1234" のBCryptハッシュを使用

USE clinic_booking_db;

-- 更新前の確認
SELECT 
    patient_id,
    patient_number,
    name,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2b$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2y$%' THEN 'BCrypt形式'
        ELSE 'その他の形式（更新が必要）'
    END as password_format_before
FROM patients
ORDER BY patient_id DESC;

-- すべての患者のパスワードを "test1234" に更新（BCrypt形式）
-- パスワード "test1234" のBCryptハッシュ: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC
UPDATE patients
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC'
WHERE 1=1;

-- 更新後の確認
SELECT 
    patient_id,
    patient_number,
    name,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2b$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2y$%' THEN 'BCrypt形式'
        ELSE 'その他の形式'
    END as password_format_after
FROM patients
ORDER BY patient_id DESC;

-- 更新された患者数を確認
SELECT COUNT(*) as updated_patients_count
FROM patients
WHERE password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC';
