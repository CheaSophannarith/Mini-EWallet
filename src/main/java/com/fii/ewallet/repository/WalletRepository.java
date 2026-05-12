package com.fii.ewallet.repository;

import com.fii.ewallet.entity.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

    Wallet findByUserId(Long userId);

    Wallet findByWalletId(String walletId);

}
