-- bookingsテーブルから古いtime_slotカラムを削除するSQL
USE clinic_booking_db;

-- 現在のテーブル構造を確認
DESCRIBE bookings;

-- time_slotカラムを削除（booking_timeカラムが既に存在するため）
ALTER TABLE bookings 
DROP COLUMN time_slot;

-- 確認
DESCRIBE bookings;
