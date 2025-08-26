package com.edira.edira_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ApiErrorAuthenticationEntryPoint entryPoint,
            ApiErrorAccessDeniedHandler deniedHandler
    ) throws Exception {

        return http
                // API stateless
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

                // handlers a json
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)   // 401
                        .accessDeniedHandler(deniedHandler))    // 403

                // qué paths no requieren auth (para poder levantar y revisar health/docs)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // habilita Basic para poder probar fácil con usuario por defecto de Spring
                .httpBasic(org.springframework.security.config.Customizer.withDefaults())

                .build();
    }

}
