package com.clinic.booking.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * test1234のBCryptハッシュを生成するユーティリティ
 */
public class GenerateTest1234Hash {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "test1234";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("========================================");
        System.out.println("パスワード: " + password);
        System.out.println("BCryptハッシュ: " + hashedPassword);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL UPDATE文:");
        System.out.println("UPDATE patients SET password = '" + hashedPassword + "' WHERE 1=1;");
        System.out.println();
        System.out.println("検証:");
        System.out.println("マッチするか: " + encoder.matches(password, hashedPassword));
    }
}
