-- time_slotカラムをbooking_timeカラムに移行するSQL
USE clinic_booking_db;

-- 1. booking_timeカラムを追加（NULL許可で一時的に追加）
ALTER TABLE bookings 
ADD COLUMN booking_time TIME NULL AFTER booking_date;

-- 2. 既存のtime_slotデータをbooking_timeに変換
-- time_slotが'HH:mm'形式（例: '09:00'）の場合
UPDATE bookings 
SET booking_time = CAST(CONCAT(time_slot, ':00') AS TIME)
WHERE time_slot IS NOT NULL AND time_slot != '';

-- 3. booking_timeがNULLの場合はデフォルト値を設定（念のため）
UPDATE bookings 
SET booking_time = '09:00:00'
WHERE booking_time IS NULL;

-- 4. booking_timeをNOT NULLに変更
ALTER TABLE bookings 
MODIFY COLUMN booking_time TIME NOT NULL;

-- 5. 確認: データが正しく移行されたか確認
SELECT booking_id, booking_date, time_slot, booking_time, status
FROM bookings
LIMIT 10;

-- 6. （オプション）time_slotカラムを削除する場合は以下のコマンドを実行
-- ALTER TABLE bookings DROP COLUMN time_slot;
