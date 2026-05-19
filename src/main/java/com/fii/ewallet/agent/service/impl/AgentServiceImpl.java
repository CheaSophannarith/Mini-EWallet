package com.fii.ewallet.agent.service.impl;

import com.fii.ewallet.agent.dto.AddMoneyToUserRequest;
import com.fii.ewallet.agent.dto.TransactionListResponse;
import com.fii.ewallet.agent.service.AgentService;
import com.fii.ewallet.entity.Transaction;
import com.fii.ewallet.entity.User;
import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.enums.Role;
import com.fii.ewallet.enums.TransactionType;
import com.fii.ewallet.exception.InsufficientBalanceException;
import com.fii.ewallet.exception.TransactionLimitExceededException;
import com.fii.ewallet.exception.WalletNotFoundException;
import com.fii.ewallet.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.repository.WalletRepository;
import com.fii.ewallet.wallet.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public void AddMoneyToUserWallet(String email, AddMoneyToUserRequest addMoneyToUserRequest) {

        User agent = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Agent not found")
        );

        Wallet userWallet = walletRepository.findByWalletId(addMoneyToUserRequest.walletId());
        if (userWallet == null){
            throw new WalletNotFoundException("User wallet not found");
        }

        if (!userWallet.getUser().getRole().equals(Role.USER.name())){
            throw new IllegalArgumentException("Target wallet does not belong to a user");
        }

        Wallet agentWallet = agent.getWallet();
        if (agentWallet == null) {
            throw new WalletNotFoundException("Agent wallet not found");
        }

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<Transaction> recentTransactions = transactionRepository
                .findBySenderIdAndCreatedAtAfter(agent.getId(), oneMinuteAgo);
        if (recentTransactions.size() >= 5) {
            throw new TransactionLimitExceededException("Agent can only deposit 5 times per minute");
        }

        if (addMoneyToUserRequest.amount().compareTo(agentWallet.getBalance()) > 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setSender(agent);
        transaction.setReceiver(userWallet.getUser());
        transaction.setAmount(addMoneyToUserRequest.amount().doubleValue());

        transactionRepository.save(transaction);

        agentWallet.setBalance(agentWallet.getBalance().subtract(addMoneyToUserRequest.amount()));
        userWallet.setBalance(userWallet.getBalance().add(addMoneyToUserRequest.amount()));

        walletRepository.save(agentWallet);
        walletRepository.save(userWallet);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TransactionListResponse> getTransactions(String email, int page, int size) {

        User agent = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Agent not found")
        );

        Page<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(agent.getId(), agent.getId(), PageRequest.of(page, size));

        Page<TransactionListResponse> transactionListResponses = transactions.map(tx -> {
            boolean isOut = tx.getSender().getId().equals(agent.getId());
            TransactionType type = isOut ? TransactionType.OUT : TransactionType.IN;
            String counterpartName = isOut ? tx.getReceiver().getName() : tx.getSender().getName();

            return new TransactionListResponse(
                    tx.getId(),
                    tx.getAmount(),
                    counterpartName,
                    type,
                    tx.getCreatedAt()
            );
        });

        return transactionListResponses;

    }
}