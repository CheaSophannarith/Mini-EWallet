package com.fii.ewallet.config;

import com.fii.ewallet.entity.User;
import com.fii.ewallet.enums.Role;
import com.fii.ewallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@ewallet.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@ewallet.com");
            admin.setPassword(passwordEncoder.encode("Admin@12345"));
            admin.setRole(Role.ADMIN.name());
            admin.setVerified(true);
            userRepository.save(admin);
        }
    }
}
