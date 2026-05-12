package com.fii.ewallet.admin.service.impl;

import com.fii.ewallet.admin.dto.TransactionListResponse;
import com.fii.ewallet.admin.dto.TransactionResponse;
import com.fii.ewallet.admin.service.AdminService;
import com.fii.ewallet.entity.Transaction;
import com.fii.ewallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionListResponse> getTransactions(int page, int size) {

        return transactionRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(tx -> new TransactionListResponse(
                        tx.getId(),
                        BigDecimal.valueOf(tx.getAmount()),
                        String.valueOf(tx.getSender().getId()),
                        String.valueOf(tx.getReceiver().getId()),
                        tx.getSender().getName(),
                        tx.getReceiver().getName(),
                        tx.getCreatedAt()
                ));
    }

    @Override
    public TransactionResponse getTransaction(Long id) {

        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Transaction not found")
        );

        return new TransactionResponse(
                BigDecimal.valueOf(transaction.getId()),
                String.valueOf(transaction.getSender().getId()),
                String.valueOf(transaction.getReceiver().getId()),
                transaction.getSender().getName(),
                transaction.getReceiver().getName(),
                transaction.getCreatedAt()
        );

    }
}
