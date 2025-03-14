package com.emprendev.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Consider re-enabling CSRF protection in production
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login").permitAll() // Allow login without authentication
                        .requestMatchers("/mipyme/**").hasRole("MIPYME") // Restrict access to Mipyme users
                        .requestMatchers("/dev/**").hasRole("DEV") // Restrict access to Dev users

                )
                .formLogin(formLogin -> formLogin

                        .loginPage("/login") // Login form endpoint
                        .defaultSuccessUrl("/determineRedirect", true)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/logout?expired")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")// Logout endpoint
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        UserDetails mipymeUser = User.withDefaultPasswordEncoder()
                .username("mipymeUser")
                .password("password")
                .roles("MIPYME")
                .build();

        UserDetails devUser = User.withDefaultPasswordEncoder()
                .username("devUser")
                .password("password")
                .roles("DEV")
                .build();

        return new InMemoryUserDetailsManager(user, mipymeUser, devUser);
    }
}