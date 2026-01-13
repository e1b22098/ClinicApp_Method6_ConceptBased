package com.clinic.booking.mapper;

import com.clinic.booking.model.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PatientMapper {
    Optional<Patient> findByPatientId(@Param("patientId") Integer patientId);
    Optional<Patient> findByPatientNumber(@Param("patientNumber") String patientNumber);
    Optional<Patient> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    String findMaxPatientNumber();
    void insert(Patient patient);
    void update(Patient patient);
    void updatePassword(@Param("patientId") Integer patientId, @Param("password") String password);
    Optional<Patient> findByResetToken(@Param("resetToken") String resetToken);
    java.util.List<Patient> findAll();
}
