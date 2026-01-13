package com.clinic.booking.security;

import com.clinic.booking.service.AdminService;
import com.clinic.booking.service.PatientService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PatientService patientService;
    private final AdminService adminService;

    public CustomAuthenticationProvider(PatientService patientService, AdminService adminService) {
        this.patientService = patientService;
        this.adminService = adminService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 管理者認証を試行
        if (adminService.authenticate(username, password)) {
            return new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 患者認証を試行
        if (patientService.authenticate(username, password)) {
            return new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"))
            );
        }

        throw new BadCredentialsException("認証に失敗しました");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
