package com.fii.ewallet.transaction.controller;

import com.fii.ewallet.transaction.dto.SendMoneyRequest;
import com.fii.ewallet.transaction.dto.SendMoneyResponse;
import com.fii.ewallet.transaction.dto.TransactionListResponse;
import com.fii.ewallet.transaction.dto.TransactionResponse;
import com.fii.ewallet.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/send")
    public ResponseEntity<SendMoneyResponse> sendMoney(@Valid @RequestBody SendMoneyRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SendMoneyResponse response = transactionService.sendMoney(request, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionListResponse>> getTransactions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(transactionService.getTransactions(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        TransactionResponse response = transactionService.getTransaction(id, email);

        return ResponseEntity.ok(response);
    }
}
