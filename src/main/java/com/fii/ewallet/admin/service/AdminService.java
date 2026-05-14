package com.fii.ewallet.admin.service;

import com.fii.ewallet.admin.dto.*;
import org.springframework.data.domain.Page;

public interface AdminService {

    Page<TransactionListResponse> getTransactions(int page, int size);

    TransactionResponse getTransaction(Long id);

    Page<UserListResponse> getUsers(int page, int size);

    UserResponse getUser(Long id);

    Page<UserTransactionListResponse> getUserTransactionById(Long id, int page, int size);

}
