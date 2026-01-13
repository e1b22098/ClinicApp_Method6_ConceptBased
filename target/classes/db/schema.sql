-- データベース作成
CREATE DATABASE IF NOT EXISTS clinic_booking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE clinic_booking_db;

-- 患者テーブル
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    reset_token VARCHAR(255) NULL,
    reset_token_expiry DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 管理者テーブル
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id_string VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 営業日テーブル
CREATE TABLE IF NOT EXISTS business_days (
    business_day_id INT AUTO_INCREMENT PRIMARY KEY,
    business_date DATE NOT NULL UNIQUE,
    is_accepting BOOLEAN NOT NULL DEFAULT TRUE,
    time_slots TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 予約テーブル
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    UNIQUE KEY uk_booking_datetime (booking_date, booking_time, status),
    INDEX idx_booking_date (booking_date),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初期管理者データ（パスワード: admin123）
INSERT INTO admins (admin_id_string, password) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwC')
ON DUPLICATE KEY UPDATE admin_id_string = admin_id_string;
