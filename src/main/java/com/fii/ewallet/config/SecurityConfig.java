package com.fii.ewallet.config;

import com.fii.ewallet.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/v1/auth/change-password").authenticated()
                .requestMatchers("/api/v1/auth/**", "/error").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/agent/**").hasRole("AGENT")
                .requestMatchers("/api/v1/profile/**", "/api/v1/wallet/**", "/api/v1/transactions/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    //InMemoryUserDetailsManager is used to load users from memory
    //        @Bean
    //        public UserDetailsService userDetailsService(DataSource dataSource) {
    //
    //            UserDetails user = User.withUsername("user").password("{noop}User@12345678").roles("read").build();
    //            UserDetails admin = User.withUsername("admin").password("{bcrypt}$2a$12$zD63jzRla3fq.ft/EIVlReSWwl1T2rl4hz77EcjRRlDDGubFcfMUG").roles("admin").build();
    //
    //            return new InMemoryUserDetailsManager(admin, user);
    //
    //        }

    //JdbcUserDetailsManager is used to load users from database
    //    @Bean
    //    public UserDetailsService userDetailsService(DataSource dataSource) {
    //
    //        return new JdbcUserDetailsManager(dataSource);
    //
    //    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {

        return new HaveIBeenPwnedRestApiPasswordChecker();

    }

}
