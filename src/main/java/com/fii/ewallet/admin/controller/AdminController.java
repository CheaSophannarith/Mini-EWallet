package com.fii.ewallet.admin.controller;

import com.fii.ewallet.admin.dto.TransactionListResponse;
import com.fii.ewallet.admin.dto.TransactionResponse;
import com.fii.ewallet.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionListResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getTransactions(page, size));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id
    ){

            TransactionResponse response = adminService.getTransaction(id);

            return ResponseEntity.ok(response);

    }

}
