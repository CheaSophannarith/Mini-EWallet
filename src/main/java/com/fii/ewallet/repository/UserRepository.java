package com.fii.ewallet.repository;

import com.fii.ewallet.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findFirstByRole(String role);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);

    Page<User> findAllByRoleNotInOrderByIdDesc(List<String> roles, Pageable pageable);

    long countByRole(String role);

    long countByRoleAndCreatedAtBetween(String role, LocalDateTime start, LocalDateTime end);

}
