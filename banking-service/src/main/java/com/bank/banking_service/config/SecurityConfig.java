package com.bank.banking_service.config;

import com.bank.banking_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // Bank Accounts
                        .requestMatchers("/banking/accounts/my").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/accounts/request").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/accounts/pending").hasAuthority("ROLE_BANKER")
                        .requestMatchers("/banking/accounts/*/approval").hasAuthority("ROLE_BANKER")
                        .requestMatchers("/banking/accounts/all").hasAuthority("ROLE_BANKER")

                        // Cards
                        .requestMatchers("/banking/cards/my").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/cards/debit").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/cards/credit").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/cards/pending").hasAuthority("ROLE_BANKER")
                        .requestMatchers("/banking/cards/{cardId}/approve").hasAuthority("ROLE_BANKER")

                        // Transactions
                        .requestMatchers("/banking/transactions/my").hasAuthority("ROLE_CLIENT")
                        .requestMatchers("/banking/transactions/all").hasAuthority("ROLE_BANKER")
                        .requestMatchers("/banking/transactions/transfer").hasAuthority("ROLE_CLIENT")


                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        return http.build();
    }
}

