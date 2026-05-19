package com.fii.ewallet.repository;

import com.fii.ewallet.entity.EmailVerification;
import com.fii.ewallet.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByToken(String token);

    @Modifying
    @Query("DELETE FROM EmailVerification ev WHERE ev.user = :user")
    void deleteByUser(@Param("user") User user);

}
