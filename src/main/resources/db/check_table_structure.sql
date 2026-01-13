-- テーブル構造を確認するSQL
USE clinic_booking_db;

-- patientsテーブルの構造を確認
DESCRIBE patients;

-- または
SHOW COLUMNS FROM patients;

-- 患者データを確認（カラム名を確認してから実行）
SELECT * FROM patients LIMIT 5;
