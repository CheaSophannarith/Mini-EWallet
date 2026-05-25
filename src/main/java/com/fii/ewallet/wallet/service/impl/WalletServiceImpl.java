package com.fii.ewallet.wallet.service.impl;

import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.exception.ResourceNotFoundException;
import com.fii.ewallet.exception.WalletNotFoundException;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.repository.WalletRepository;
import com.fii.ewallet.wallet.dto.WalletResponse;
import com.fii.ewallet.wallet.service.WalletService;
import com.fii.ewallet.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Override
    public void createWallet(User user) {

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(5.00));
        wallet.setWalletId(String.format("%010d", user.getId()));

        walletRepository.save(wallet);

    }

    @Override
    public WalletResponse getWallet(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        Wallet wallet = walletRepository.findByUserId(user.getId());

        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found");
        }

        WalletResponse response = new WalletResponse(
                wallet.getId(), wallet.getBalance(), wallet.getWalletId()
        );

        return response;

    }
}
