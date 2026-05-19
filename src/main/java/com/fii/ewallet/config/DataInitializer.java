package com.fii.ewallet.config;

import com.fii.ewallet.entity.User;
import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.enums.Role;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail("admin@ewallet.com").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@ewallet.com");
            admin.setPassword(passwordEncoder.encode("Admin@12345"));
            admin.setRole(Role.ADMIN.name());
            admin.setVerified(true);
            admin = userRepository.save(admin);
        }

        Wallet adminWallet = walletRepository.findByUserId(admin.getId());
        if (adminWallet == null) {
            Wallet wallet = new Wallet();
            wallet.setUser(admin);
            wallet.setWalletId(String.format("%010d", admin.getId()));
            wallet.setBalance(BigDecimal.valueOf(99_999_999));
            walletRepository.save(wallet);
        }
    }
}
