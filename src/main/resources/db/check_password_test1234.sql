-- 既存の患者データのパスワードが "test1234" になっているか確認するSQL
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
        ELSE 'その他の形式'
    END as password_format
FROM patients
ORDER BY patient_id DESC;

-- パスワードが "test1234" のBCryptハッシュと一致するか確認
-- 注意: BCryptハッシュは毎回異なるため、完全一致では確認できません
-- 実際の検証はアプリケーション側で行う必要があります

-- パスワードがBCrypt形式かどうかを確認
SELECT 
    COUNT(*) as total_patients,
    SUM(CASE WHEN password LIKE '$2a$%' OR password LIKE '$2b$%' OR password LIKE '$2y$%' THEN 1 ELSE 0 END) as bcrypt_format_count,
    SUM(CASE WHEN password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' AND password NOT LIKE '$2y$%' THEN 1 ELSE 0 END) as other_format_count
FROM patients;
