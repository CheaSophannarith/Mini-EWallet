package com.fii.ewallet.wallet.service.impl;

import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.repository.WalletRepository;
import com.fii.ewallet.wallet.service.WalletService;
import com.fii.ewallet.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public void createWallet(User user) {

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.valueOf(5.00));
        wallet.setWalletId(String.format("%010d", user.getId()));

        walletRepository.save(wallet);

    }

}
