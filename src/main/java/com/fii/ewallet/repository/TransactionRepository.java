package com.fii.ewallet.repository;

import com.fii.ewallet.entity.Transaction;
import com.fii.ewallet.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderIdAndCreatedAtAfter(Long senderId, LocalDateTime after);

    List<Transaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Transaction> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId, Pageable pageable);

        long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

        long countByStatusAndCreatedAtBetween(Status status, LocalDateTime start, LocalDateTime end);

        long countBySenderIdAndCreatedAtBetween(Long senderId, LocalDateTime start, LocalDateTime end);

        @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.createdAt between :start and :end")
        java.math.BigDecimal sumAmountByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.sender.id = :senderId and t.createdAt between :start and :end")
        java.math.BigDecimal sumAmountBySenderIdAndCreatedAtBetween(
            @Param("senderId") Long senderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );

        @Query(value = "select date(created_at) as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where created_at between :start and :end group by date(created_at) order by date(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateAllByDay(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query(value = "select date_format(created_at, '%x-W%v') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where created_at between :start and :end group by yearweek(created_at, 3) order by yearweek(created_at, 3)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateAllByWeek(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query(value = "select date_format(created_at, '%Y-%m') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where created_at between :start and :end group by year(created_at), month(created_at) order by year(created_at), month(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateAllByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query(value = "select date_format(created_at, '%Y') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where created_at between :start and :end group by year(created_at) order by year(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateAllByYear(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query(value = "select date(created_at) as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where sender_id = :senderId and created_at between :start and :end " +
            "group by date(created_at) order by date(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateByDayForSender(
            @Param("senderId") Long senderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );

        @Query(value = "select date_format(created_at, '%x-W%v') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where sender_id = :senderId and created_at between :start and :end " +
            "group by yearweek(created_at, 3) order by yearweek(created_at, 3)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateByWeekForSender(
            @Param("senderId") Long senderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );

        @Query(value = "select date_format(created_at, '%Y-%m') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where sender_id = :senderId and created_at between :start and :end " +
            "group by year(created_at), month(created_at) order by year(created_at), month(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateByMonthForSender(
            @Param("senderId") Long senderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );

        @Query(value = "select date_format(created_at, '%Y') as bucket, count(*) as count, coalesce(sum(amount), 0) as volume " +
            "from transactions where sender_id = :senderId and created_at between :start and :end " +
            "group by year(created_at) order by year(created_at)",
            nativeQuery = true)
        java.util.List<TimeBucketAggregate> aggregateByYearForSender(
            @Param("senderId") Long senderId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
        );

}
