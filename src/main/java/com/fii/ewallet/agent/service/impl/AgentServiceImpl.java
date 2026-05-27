package com.fii.ewallet.agent.service.impl;

import com.fii.ewallet.agent.dto.AddMoneyToUserRequest;
import com.fii.ewallet.agent.dto.AgentDashboardSeriesPoint;
import com.fii.ewallet.agent.dto.AgentDashboardSeriesResponse;
import com.fii.ewallet.agent.dto.AgentDashboardSummaryResponse;
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
import com.fii.ewallet.repository.TimeBucketAggregate;
import com.fii.ewallet.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.fii.ewallet.repository.UserRepository;
import com.fii.ewallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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
        transaction.setAmount(addMoneyToUserRequest.amount());

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

    @Transactional(readOnly = true)
    @Override
    public AgentDashboardSummaryResponse getDashboardSummary(String email, String range) {

        User agent = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Agent not found")
        );

        if (!Role.AGENT.name().equals(agent.getRole())) {
            throw new IllegalArgumentException("User is not an agent");
        }

        DateRange dateRange = resolveRange(range);

        BigDecimal balance = BigDecimal.ZERO;
        if (agent.getWallet() != null && agent.getWallet().getBalance() != null) {
            balance = agent.getWallet().getBalance();
        }

        long depositCount = transactionRepository.countBySenderIdAndCreatedAtBetween(
                agent.getId(),
                dateRange.start,
                dateRange.end
        );

        BigDecimal depositVolume = transactionRepository.sumAmountBySenderIdAndCreatedAtBetween(
                agent.getId(),
                dateRange.start,
                dateRange.end
        );

        return new AgentDashboardSummaryResponse(balance, depositCount, depositVolume);
    }

    @Transactional(readOnly = true)
    @Override
    public AgentDashboardSeriesResponse getDepositsSeries(String email, String range, String bucket) {

        User agent = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Agent not found")
        );

        if (!Role.AGENT.name().equals(agent.getRole())) {
            throw new IllegalArgumentException("User is not an agent");
        }

        DateRange dateRange = resolveRange(range);

        List<TimeBucketAggregate> aggregates = resolveAgentBucket(bucket, agent.getId(), dateRange);

        List<AgentDashboardSeriesPoint> points = aggregates.stream()
                .map(item -> new AgentDashboardSeriesPoint(
                        item.getBucket(),
                        item.getCount() == null ? 0L : item.getCount(),
                        item.getVolume() == null ? BigDecimal.ZERO : item.getVolume()
                ))
                .toList();

        return new AgentDashboardSeriesResponse(bucket.toLowerCase(Locale.ROOT), points);
    }

    private DateRange resolveRange(String range) {
        String normalized = range == null ? "" : range.toLowerCase(Locale.ROOT).trim();
        LocalDateTime end = LocalDateTime.now();

        return switch (normalized) {
            case "today" -> new DateRange(LocalDate.now().atStartOfDay(), end);
            case "7d" -> new DateRange(end.minusDays(7), end);
            case "30d" -> new DateRange(end.minusDays(30), end);
            default -> throw new IllegalArgumentException("Invalid range. Use today, 7d, or 30d.");
        };
    }

    private List<TimeBucketAggregate> resolveAgentBucket(String bucket, Long agentId, DateRange range) {
        String normalized = bucket == null ? "" : bucket.toLowerCase(Locale.ROOT).trim();

        return switch (normalized) {
            case "day" -> transactionRepository.aggregateByDayForSender(agentId, range.start, range.end);
            case "week" -> transactionRepository.aggregateByWeekForSender(agentId, range.start, range.end);
            case "month" -> transactionRepository.aggregateByMonthForSender(agentId, range.start, range.end);
            case "year" -> transactionRepository.aggregateByYearForSender(agentId, range.start, range.end);
            default -> throw new IllegalArgumentException("Invalid bucket. Use day, week, month, or year.");
        };
    }

    private static class DateRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        private DateRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
    }
}