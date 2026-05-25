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
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;

    @Value("${app.bootstrap.enabled:true}")
    private boolean bootstrapEnabled;

    @Value("${app.bootstrap.admin.email:admin@ewallet.com}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:Admin@12345}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.balance:99999999}")
    private long adminBalance;

    @Override
    public void run(String... args) {
        if (!bootstrapEnabled) {
            return;
        }

        User admin = userRepository.findByEmail(adminEmail).orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN.name());
            admin.setVerified(true);
            admin = userRepository.save(admin);
        }

        Wallet adminWallet = walletRepository.findByUserId(admin.getId());
        if (adminWallet == null) {
            Wallet wallet = new Wallet();
            wallet.setUser(admin);
            wallet.setWalletId(String.format("%010d", admin.getId()));
            wallet.setBalance(BigDecimal.valueOf(adminBalance));
            walletRepository.save(wallet);
        }
    }
}
