package com.fii.ewallet.admin.service;

import com.fii.ewallet.admin.dto.TransactionListResponse;
import com.fii.ewallet.admin.dto.TransactionResponse;
import org.springframework.data.domain.Page;

public interface AdminService {

    Page<TransactionListResponse> getTransactions(int page, int size);

    TransactionResponse getTransaction(Long id);



}
