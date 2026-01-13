-- bookingテーブルのstatus値を修正するSQL
USE clinic_booking_db;

-- 現在のstatus値を確認
SELECT DISTINCT status FROM bookings;

-- status値を「ACTIVE」または「CANCELLED」に統一
-- 「予約済」を「ACTIVE」に変更
UPDATE bookings 
SET status = 'ACTIVE'
WHERE status = '予約済' OR status = '予約済み';

-- 「キャンセル」を「CANCELLED」に変更
UPDATE bookings 
SET status = 'CANCELLED'
WHERE status = 'キャンセル' OR status = 'キャンセル済み';

-- 確認
SELECT booking_id, booking_date, booking_time, status
FROM bookings;
