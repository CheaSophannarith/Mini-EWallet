package com.fii.ewallet.agent.controller;

import com.fii.ewallet.agent.dto.AddMoneyToUserRequest;
import com.fii.ewallet.agent.dto.TransactionListResponse;
import com.fii.ewallet.agent.service.AgentService;
import com.fii.ewallet.common.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/agent")
@AllArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/add-balance")
    public ResponseEntity<ApiResponse> addBalanceToUser(@RequestBody AddMoneyToUserRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        agentService.AddMoneyToUserWallet(email, request);

        return ResponseEntity.ok(new ApiResponse("Money added to user successfully", 200, LocalDateTime.now()));

    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionListResponse>> getAgentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        return ResponseEntity.ok(agentService.getTransactions(email, page, size));

    }

}
