-- reset_tokenとreset_token_expiryカラムを追加するSQL
-- パスワードリセット機能を使用する場合は、このSQLを実行してください

USE clinic_booking_db;

-- reset_tokenカラムを追加（既に存在する場合はエラーになるが問題なし）
ALTER TABLE patients 
ADD COLUMN reset_token VARCHAR(255) NULL;

-- reset_token_expiryカラムを追加（既に存在する場合はエラーになるが問題なし）
ALTER TABLE patients 
ADD COLUMN reset_token_expiry DATE NULL;
