package com.fii.ewallet.wallet.service;

import com.fii.ewallet.entity.User;
import com.fii.ewallet.wallet.dto.WalletResponse;

public interface WalletService {

    void createWallet(User user);
    WalletResponse getWallet(String email);

}
