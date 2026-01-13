package com.clinic.booking.service;

import com.clinic.booking.mapper.AdminMapper;
import com.clinic.booking.model.Admin;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminMapper adminMapper, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String adminIdString, String rawPassword) {
        Optional<Admin> adminOpt = adminMapper.findByAdminIdString(adminIdString);
        if (adminOpt.isEmpty()) {
            return false;
        }
        Admin admin = adminOpt.get();
        return passwordEncoder.matches(rawPassword, admin.getPassword());
    }

    public Optional<Admin> findByAdminIdString(String adminIdString) {
        return adminMapper.findByAdminIdString(adminIdString);
    }
}
