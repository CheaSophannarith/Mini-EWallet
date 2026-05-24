package com.fii.ewallet.data.service.impl;

import com.fii.ewallet.data.dto.UsernameResponse;
import com.fii.ewallet.data.service.DataService;
import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DataServiceImpl implements DataService{

    private final WalletRepository walletRepository;

    @Override
    public UsernameResponse getWalletName(String walletId) {

        Wallet wallet = walletRepository.findByWalletId(walletId);

        if (wallet == null) {
            return new UsernameResponse("");
        }

        UsernameResponse username = new UsernameResponse(
                wallet.getUser().getName()
        );

        return username;

    }
}
