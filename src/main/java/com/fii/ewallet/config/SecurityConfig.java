package com.fii.ewallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/v1/auth/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

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
