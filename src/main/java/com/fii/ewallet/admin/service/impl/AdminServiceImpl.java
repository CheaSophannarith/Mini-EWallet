package com.fii.ewallet.admin.service.impl;

import com.fii.ewallet.admin.dto.*;
import com.fii.ewallet.admin.service.AdminService;
import com.fii.ewallet.entity.Transaction;
import com.fii.ewallet.entity.User;
import com.fii.ewallet.entity.Wallet;
import com.fii.ewallet.enums.Role;
import com.fii.ewallet.enums.TransactionType;
import com.fii.ewallet.exception.EmailAlreadyInUsedException;
import com.fii.ewallet.exception.EmailIsNotVerified;
import com.fii.ewallet.repository.TransactionRepository;
import com.fii.ewallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public Page<UserListResponse> getUsers(int page, int size) {

        List<String> excludedRoles = List.of(Role.ADMIN.name(), Role.AGENT.name());
        Page<User> users = userRepository.findAllByRoleNotInOrderByIdDesc(
            excludedRoles,
            PageRequest.of(page, size)
        );

        Page<UserListResponse> userList = users.map(user -> {
            Wallet wallet = user.getWallet();
            String walletId = wallet != null ? wallet.getWalletId() : null;

            return new UserListResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    walletId,
                    user.getRole()
            );
        });

        return userList;

    }

    @Override
    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getWallet().getWalletId(),
                user.getRole()
        );

    }

    @Override
    public Page<UserTransactionListResponse> getUserTransactionById(Long id, int page, int size) {

        Page<Transaction> userTransactions = transactionRepository
                .findBySenderIdOrReceiverIdOrderByCreatedAtDesc(id, id, PageRequest.of(page, size));

        return userTransactions.map(tx -> {
            boolean isOut = tx.getSender().getId().equals(id);
            TransactionType type = isOut ? TransactionType.OUT : TransactionType.IN;
            String counterpartName = isOut ? tx.getReceiver().getName() : tx.getSender().getName();

            return new UserTransactionListResponse(
                    tx.getId(),
                    BigDecimal.valueOf(tx.getAmount()),
                    counterpartName,
                    type,
                    tx.getCreatedAt().toLocalDate()
            );
        });
    }

    @Override
    public void createAgentUser(AgentUserRequest request) {

        User existing = userRepository.findByEmail(request.email()).orElse(null);

        if (existing != null && existing.isVerified()) {
            throw new EmailAlreadyInUsedException("Email already taken");
        }

        if (existing != null && !existing.isVerified()) {
            throw new EmailIsNotVerified("Email is not verified");
        }

        User createdAgent = new User();
        createdAgent.setName(request.name());
        createdAgent.setEmail(request.email());
        createdAgent.setPassword(passwordEncoder.encode(request.password()));
        createdAgent.setVerified(true);
        createdAgent.setRole(Role.AGENT.name());

        userRepository.save(createdAgent);

    }

    @Override
    public Page<AgentUserResponse> getAgents(int page, int size) {

        List<String> excludedRoles = List.of(Role.ADMIN.name(), Role.USER.name());
        Page<User> users = userRepository.findAllByRoleNotInOrderByIdDesc(
                excludedRoles,
                PageRequest.of(page, size)
        );

        Page<AgentUserResponse> agentUserResponses = users
                .map(user -> {
                    return new AgentUserResponse(
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getCreatedAt()
                    );
                });

        return agentUserResponses;

    }
}
