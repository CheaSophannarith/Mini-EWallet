package com.fii.ewallet.repository;

import com.fii.ewallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderIdAndCreatedAtAfter(Long senderId, LocalDateTime after);

    List<Transaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
