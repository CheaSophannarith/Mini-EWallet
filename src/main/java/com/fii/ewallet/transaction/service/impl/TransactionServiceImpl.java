package com.fii.ewallet.transaction.service.impl;

import com.fii.ewallet.entity.Transaction;
import com.fii.ewallet.entity.User;
import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.enums.Status;
import com.fii.ewallet.exception.InsufficientBalanceException;
import com.fii.ewallet.exception.TransactionLimitExceededException;
import com.fii.ewallet.exception.WalletNotFoundException;
import com.fii.ewallet.repository.TransactionRepository;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.repository.WalletRepository;
import com.fii.ewallet.enums.TransactionType;
import com.fii.ewallet.transaction.dto.SendMoneyRequest;
import com.fii.ewallet.transaction.dto.SendMoneyResponse;
import com.fii.ewallet.transaction.dto.TransactionListResponse;
import com.fii.ewallet.transaction.dto.TransactionResponse;
import com.fii.ewallet.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public SendMoneyResponse sendMoney(SendMoneyRequest request, String senderEmail) {

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Wallet senderWallet = walletRepository.findByUserId(sender.getId());
        if (senderWallet == null) {
            throw new WalletNotFoundException("Sender wallet not found");
        }

        Wallet receiverWallet = walletRepository.findByWalletId(request.receiverWalletId());
        if (receiverWallet == null) {
            throw new WalletNotFoundException("Receiver wallet not found");
        }

        if (senderWallet.getWalletId().equals(request.receiverWalletId())) {
            throw new IllegalArgumentException("Cannot send money to your own wallet");
        }

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        List<Transaction> recentTransactions = transactionRepository
                .findBySenderIdAndCreatedAtAfter(sender.getId(), oneMinuteAgo);
        if (recentTransactions.size() >= 5) {
            throw new TransactionLimitExceededException("You can only send money 5 times per minute");
        }

        BigDecimal amount = request.amount();
        BigDecimal senderBalance = senderWallet.getBalance();

        if (amount.compareTo(BigDecimal.valueOf(1000)) > 0) {
            BigDecimal maxAllowed = senderBalance.multiply(BigDecimal.valueOf(0.80));
            if (amount.compareTo(maxAllowed) > 0) {
                throw new InsufficientBalanceException(
                        "Transactions over 1000 cannot exceed 80% of your balance"
                );
            }
        }

        if (senderBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        senderWallet.setBalance(senderBalance.subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiverWallet.getUser());
        transaction.setAmount(amount.doubleValue());
        transaction.setStatus(Status.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return new SendMoneyResponse(
                "Money sent successfully",
                senderWallet.getBalance(),
                transaction.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionListResponse> getTransactions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return transactionRepository
                .findBySenderIdOrReceiverIdOrderByCreatedAtDesc(user.getId(), user.getId())
                .stream()
                .map(tx -> {
                    boolean isOut = tx.getSender().getId().equals(user.getId());
                    TransactionType type = isOut ? TransactionType.OUT : TransactionType.IN;
                    String counterpartName = isOut ? tx.getReceiver().getName() : tx.getSender().getName();
                    return new TransactionListResponse(
                            tx.getId(),
                            BigDecimal.valueOf(tx.getAmount()),
                            counterpartName,
                            type
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long transactionId, String email) {
        
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new RuntimeException("Transaction not found")
        );
        
        if (!transaction.getSender().getId().equals(user.getId()) && !transaction.getReceiver().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to view this transaction");
        }

        TransactionResponse response = getTransactionResponse(transaction, user);

        return response;
        
    }

    private static @NonNull TransactionResponse getTransactionResponse(Transaction transaction, User user) {
        boolean isOut = transaction.getSender().getId().equals(user.getId());
        String counterpartName = isOut ? transaction.getReceiver().getName() : transaction.getSender().getName();
        TransactionType type = isOut ? TransactionType.OUT : TransactionType.IN;

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                counterpartName,
                transaction.getAmount(),
                type,
                transaction.getCreatedAt()
        );
        return response;
    }
}
