package com.fii.ewallet.data.service;

import com.fii.ewallet.data.dto.UsernameResponse;

public interface DataService {

    UsernameResponse getWalletName(String walletId);

}
