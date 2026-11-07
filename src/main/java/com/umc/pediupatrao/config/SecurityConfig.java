package com.umc.pediupatrao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // Habilita segurança por anotações (@PreAuthorize) nos Controllers e Services
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**").permitAll()

                        .requestMatchers("/usuarios/**", "/api/usuarios/**").hasRole("ADMIN")
                        //adicionando rota dash
                        .requestMatchers("/dashboard", "/api/v1/dashboard/**").hasAnyRole("ADMIN", "GERENTE")

                        .requestMatchers("/auditoria/**", "/api/auditoria/**").hasAnyRole("ADMIN", "GERENTE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            // Verifica se é Admin ou Gerente
                            boolean isGestor = authentication.getAuthorities().stream()
                                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_GERENTE"));

                            if (isGestor) {
                                // Redireciona ADMIN e GERENTE para o dashboard na rota /home
                                response.sendRedirect("/home");
                            } else {
                                // Redireciona o ATENDENTE para a tela de pedidos (já que ele não pode ver o dashboard)
                                response.sendRedirect("/pedidos");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}