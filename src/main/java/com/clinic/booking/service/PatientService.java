package com.clinic.booking.service;

import com.clinic.booking.mapper.PatientMapper;
import com.clinic.booking.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientMapper patientMapper, PasswordEncoder passwordEncoder) {
        this.patientMapper = patientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Patient> findByPatientId(Integer patientId) {
        return patientMapper.findByPatientId(patientId);
    }

    public Optional<Patient> findByPatientNumber(String patientNumber) {
        return patientMapper.findByPatientNumber(patientNumber);
    }

    public Optional<Patient> findByPhoneNumber(String phoneNumber) {
        return patientMapper.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public Patient register(Patient patient) {
        // 診察券番号を自動生成（最初は0000181、その後は連番）
        String patientNumber = generatePatientNumber();
        logger.debug("生成された診察券番号: {}", patientNumber);
        patient.setPatientNumber(patientNumber);
        
        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);
        
        // データベースに挿入
        patientMapper.insert(patient);
        logger.debug("患者をデータベースに挿入しました。patientId: {}", patient.getPatientId());
        
        // 登録後にデータベースから再度取得して診察券番号を確認
        // まずpatientIdで検索を試みる
        if (patient.getPatientId() != null) {
            Optional<Patient> savedPatient = patientMapper.findByPatientId(patient.getPatientId());
            if (savedPatient.isPresent()) {
                Patient retrieved = savedPatient.get();
                logger.debug("patientIdで取得した患者: patientNumber={}", retrieved.getPatientNumber());
                // patientNumberを確実に設定（データベースから取得した値がnullの場合）
                if (retrieved.getPatientNumber() == null || retrieved.getPatientNumber().trim().isEmpty()) {
                    retrieved.setPatientNumber(patientNumber);
                    logger.debug("patientNumberがnullまたは空のため、生成した値を設定: {}", patientNumber);
                }
                return retrieved;
            }
        }
        
        // patientIdで取得できない場合は、診察券番号で検索
        Optional<Patient> savedByNumber = patientMapper.findByPatientNumber(patientNumber);
        if (savedByNumber.isPresent()) {
            Patient retrieved = savedByNumber.get();
            logger.debug("patientNumberで取得した患者: patientNumber={}", retrieved.getPatientNumber());
            // patientNumberを確実に設定（念のため）
            if (retrieved.getPatientNumber() == null || retrieved.getPatientNumber().trim().isEmpty()) {
                retrieved.setPatientNumber(patientNumber);
                logger.debug("patientNumberがnullまたは空のため、生成した値を設定: {}", patientNumber);
            }
            return retrieved;
        }
        
        // それでも取得できない場合は、設定した診察券番号を返す
        // patientNumberを確実に設定
        logger.warn("データベースから患者を取得できませんでした。生成したpatientNumberを返します: {}", patientNumber);
        patient.setPatientNumber(patientNumber);
        return patient;
    }

    /**
     * 診察券番号を生成する（最初は0000181、その後は最大値+1）
     */
    private String generatePatientNumber() {
        String maxNumber = patientMapper.findMaxPatientNumber();
        if (maxNumber == null || maxNumber.trim().isEmpty()) {
            // 最初の患者は0000181
            return "0000181";
        }
        
        // 前後の空白を削除
        maxNumber = maxNumber.trim();
        
        // 数値に変換して+1
        try {
            // 先頭の0を削除して数値に変換
            int number = Integer.parseInt(maxNumber);
            number++;
            // 7桁の0埋め形式で返す
            return String.format("%07d", number);
        } catch (NumberFormatException e) {
            // パースに失敗した場合は0000181から開始
            return "0000181";
        }
    }

    public boolean authenticate(String patientNumber, String rawPassword) {
        // 前後の空白を削除
        if (patientNumber == null || patientNumber.trim().isEmpty()) {
            return false;
        }
        patientNumber = patientNumber.trim();
        
        Optional<Patient> patientOpt = patientMapper.findByPatientNumber(patientNumber);
        if (patientOpt.isEmpty()) {
            return false;
        }
        Patient patient = patientOpt.get();
        
        if (patient.getPassword() == null || rawPassword == null) {
            return false;
        }
        
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }

    @Transactional
    public String generateResetToken(String patientNumber) {
        Optional<Patient> patientOpt = patientMapper.findByPatientNumber(patientNumber);
        if (patientOpt.isEmpty()) {
            throw new IllegalArgumentException("患者が見つかりません");
        }
        Patient patient = patientOpt.get();
        String token = UUID.randomUUID().toString();
        patient.setResetToken(token);
        patient.setResetTokenExpiry(LocalDate.now().plusDays(1));
        patientMapper.update(patient);
        return token;
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        Optional<Patient> patientOpt = patientMapper.findByResetToken(resetToken);
        if (patientOpt.isEmpty()) {
            throw new IllegalArgumentException("無効なリセットトークンです");
        }
        Patient patient = patientOpt.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        patientMapper.updatePassword(patient.getPatientId(), encodedPassword);
        patient.setResetToken(null);
        patient.setResetTokenExpiry(null);
        patientMapper.update(patient);
    }

    /**
     * すべての患者のパスワードを指定したパスワードに更新する（開発・テスト用）
     */
    @Transactional
    public int updateAllPasswords(String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        List<Patient> allPatients = patientMapper.findAll();
        int updatedCount = 0;
        System.out.println("=== パスワード更新開始 ===");
        System.out.println("新しいパスワード: " + newPassword);
        System.out.println("BCryptハッシュ: " + encodedPassword);
        System.out.println("患者数: " + allPatients.size());
        for (Patient patient : allPatients) {
            System.out.println("患者ID: " + patient.getPatientId() + ", 診察券番号: " + patient.getPatientNumber() + ", 名前: " + patient.getName());
            patientMapper.updatePassword(patient.getPatientId(), encodedPassword);
            updatedCount++;
        }
        System.out.println("更新された患者数: " + updatedCount);
        System.out.println("=== パスワード更新完了 ===");
        return updatedCount;
    }
}
