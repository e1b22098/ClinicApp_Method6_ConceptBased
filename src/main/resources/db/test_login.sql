-- ログインテスト用SQL
-- このSQLで、診察券番号とパスワードが正しく保存されているか確認できます

USE clinic_booking_db;

-- 1. すべての患者データを確認（パスワードはハッシュ化されているため表示されません）
SELECT 
    patient_id,
    patient_number,
    name,
    phone,
    LENGTH(password) as password_length,
    created_at
FROM patients
ORDER BY patient_id;

-- 2. 特定の診察券番号で検索（例: 0000181）
SELECT 
    patient_id,
    patient_number,
    name,
    phone
FROM patients
WHERE patient_number = '0000181';

-- 3. 診察券番号がNULLまたは空の患者を確認
SELECT 
    patient_id,
    patient_number,
    name,
    phone
FROM patients
WHERE patient_number IS NULL OR patient_number = '' OR TRIM(patient_number) = '';

-- 4. 診察券番号の形式を確認（先頭・末尾の空白など）
SELECT 
    patient_id,
    patient_number,
    LENGTH(patient_number) as length,
    CHAR_LENGTH(patient_number) as char_length,
    CONCAT('"', patient_number, '"') as with_quotes,
    TRIM(patient_number) as trimmed
FROM patients
ORDER BY patient_id;
