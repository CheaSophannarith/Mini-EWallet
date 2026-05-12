package com.fii.ewallet.transaction.service;

import com.fii.ewallet.transaction.dto.SendMoneyRequest;
import com.fii.ewallet.transaction.dto.SendMoneyResponse;
import com.fii.ewallet.transaction.dto.TransactionListResponse;
import com.fii.ewallet.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    SendMoneyResponse sendMoney(SendMoneyRequest request, String senderEmail);

    List<TransactionListResponse> getTransactions(String email);

    TransactionResponse getTransaction(Long transactionId, String email);

}
