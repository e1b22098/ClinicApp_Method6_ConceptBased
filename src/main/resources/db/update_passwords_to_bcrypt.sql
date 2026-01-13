-- 既存の患者データのパスワードをBCrypt形式に更新するSQL
-- 注意: このスクリプトは既存の患者データのパスワードをリセットします
-- パスワードは "password123" に設定されます（BCrypt形式）

USE clinic_booking_db;

-- パスワード形式を確認
SELECT 
    patient_id,
    patient_number,
    name,
    SUBSTRING(password, 1, 20) as password_preview,
    CASE 
        WHEN password LIKE '$2a$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2b$%' THEN 'BCrypt形式'
        WHEN password LIKE '$2y$%' THEN 'BCrypt形式'
        ELSE 'その他の形式'
    END as password_format
FROM patients
ORDER BY patient_id DESC;

-- 既存の患者データのパスワードをBCrypt形式に更新
-- パスワード "password123" のBCryptハッシュ: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC
-- 注意: 実際の運用では、各患者の元のパスワードを確認してから更新してください

-- すべての患者のパスワードを "password123" にリセット（BCrypt形式）
UPDATE patients
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC'
WHERE password NOT LIKE '$2a$%' 
   AND password NOT LIKE '$2b$%' 
   AND password NOT LIKE '$2y$%';

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
    END as password_format
FROM patients
ORDER BY patient_id DESC;
