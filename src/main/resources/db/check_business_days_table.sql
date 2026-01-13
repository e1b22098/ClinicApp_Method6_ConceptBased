-- business_daysテーブルの構造を確認するSQL
USE clinic_booking_db;

-- テーブル構造を確認
DESCRIBE business_days;

-- または
SHOW COLUMNS FROM business_days;

-- カラム一覧を確認
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'clinic_booking_db' 
AND table_name = 'business_days'
ORDER BY ordinal_position;
