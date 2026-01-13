-- bookingsテーブルの構造を確認するSQL
USE clinic_booking_db;

-- テーブル構造を確認
DESCRIBE bookings;

-- または
SHOW COLUMNS FROM bookings;

-- テーブルが存在するか確認
SELECT COUNT(*) as table_exists
FROM information_schema.tables 
WHERE table_schema = 'clinic_booking_db' 
AND table_name = 'bookings';

-- カラム一覧を確認
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'clinic_booking_db' 
AND table_name = 'bookings'
ORDER BY ordinal_position;
