package com.fii.ewallet.auth.repository;

import com.fii.ewallet.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {


    RefreshToken findByToken(String token);

}
