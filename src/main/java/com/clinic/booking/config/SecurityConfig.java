package com.clinic.booking.config;

import com.clinic.booking.security.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;

    public SecurityConfig(CustomAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/patient/register", "/patient/login", "/patient/reset-password/**", 
                               "/admin/login", "/admin/update-all-passwords", "/css/**", "/js/**").permitAll()
                .requestMatchers("/patient/**").hasRole("PATIENT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/patient/login")
                .loginProcessingUrl("/patient/login")
                .defaultSuccessUrl("/patient/dashboard", true)
                .failureUrl("/patient/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
