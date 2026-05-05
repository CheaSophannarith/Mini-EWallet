package com.fii.ewallet.email.repository;

import com.fii.ewallet.entity.EmailVerification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByToken(String token);

}