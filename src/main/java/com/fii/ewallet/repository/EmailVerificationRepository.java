package com.fii.ewallet.repository;

import com.fii.ewallet.entity.EmailVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByToken(String token);

}
