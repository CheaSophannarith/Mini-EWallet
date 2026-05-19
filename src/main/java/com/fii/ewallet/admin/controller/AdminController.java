package com.fii.ewallet.admin.controller;

import com.fii.ewallet.admin.dto.*;
import com.fii.ewallet.admin.service.AdminService;
import com.fii.ewallet.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping("/users")
    public ResponseEntity<Page<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ){
        UserResponse response = adminService.getUser(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}/transactions")
    public ResponseEntity<Page<UserTransactionListResponse>> getUserTransactionsById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminService.getUserTransactionById(id, page, size));
    }

    @PostMapping("/agents")
    public ResponseEntity<ApiResponse> createAgentUser(@Valid @RequestBody AgentUserRequest request) {

        adminService.createAgentUser(request);

        ApiResponse response = new ApiResponse(
                "Agent registered successfully",
                HttpStatus.CREATED.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(201).body(response);

    }

    @GetMapping("/agents")
    public ResponseEntity<Page<AgentUserResponse>> getAllAgentUsers(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(201).body(adminService.getAgents(page, size));

    }

    @PostMapping("/agents/{id}/wallet/balance")
    public ResponseEntity<ApiResponse> addAmountToAgentWallet(
            @PathVariable Long id,
            @Valid @RequestBody AddBalanceToAgentWalletRequest request
    ){

        adminService.addBalanceToAgentWallet(id, request);

        ApiResponse response = new ApiResponse(
                "Amount added successfully",
                HttpStatus.OK.value(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);

    }

    @GetMapping("/agents/{id}/transactions")
    public ResponseEntity<Page<AgentTransactionListResponse>> getAgentTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<AgentTransactionListResponse> agentTransactions = adminService.getAgentTransactions(id, page, size);

        return ResponseEntity.ok(agentTransactions);
    }

}
