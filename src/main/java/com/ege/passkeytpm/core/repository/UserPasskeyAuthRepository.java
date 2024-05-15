package com.ege.passkeytpm.core.repository;

import com.ege.passkeytpm.core.impl.pojo.UserPasskeyAuthImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPasskeyAuthRepository extends JpaRepository<UserPasskeyAuthImpl, Long> {
    List<UserPasskeyAuthImpl> findByExpiresAtBeforeAndIsValidTrue(LocalDateTime dateTime);
}
