package com.clinic.booking.service;

import com.clinic.booking.mapper.PatientMapper;
import com.clinic.booking.model.Patient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientMapper patientMapper, PasswordEncoder passwordEncoder) {
        this.patientMapper = patientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Patient> findByPatientId(Integer patientId) {
        return patientMapper.findByPatientId(patientId);
    }

    public Optional<Patient> findByPhoneNumber(String phoneNumber) {
        return patientMapper.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public Patient register(Patient patient) {
        String encodedPassword = passwordEncoder.encode(patient.getPassword());
        patient.setPassword(encodedPassword);
        patientMapper.insert(patient);
        return patient;
    }

    public boolean authenticate(String phoneNumber, String rawPassword) {
        Optional<Patient> patientOpt = patientMapper.findByPhoneNumber(phoneNumber);
        if (patientOpt.isEmpty()) {
            return false;
        }
        Patient patient = patientOpt.get();
        return passwordEncoder.matches(rawPassword, patient.getPassword());
    }

    @Transactional
    public String generateResetToken(String phoneNumber) {
        Optional<Patient> patientOpt = patientMapper.findByPhoneNumber(phoneNumber);
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
}
