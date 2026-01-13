package com.clinic.booking.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワードのBCryptハッシュを生成するユーティリティクラス
 * このクラスは、データベースの既存患者データのパスワードを更新する際に使用します
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "test1234";
        String hashedPassword = encoder.encode(password);
        System.out.println("パスワード: " + password);
        System.out.println("BCryptハッシュ: " + hashedPassword);
        System.out.println();
        System.out.println("SQL UPDATE文:");
        System.out.println("UPDATE patients SET password = '" + hashedPassword + "' WHERE password NOT LIKE '$2a$%' AND password NOT LIKE '$2b$%' AND password NOT LIKE '$2y$%';");
    }
}
