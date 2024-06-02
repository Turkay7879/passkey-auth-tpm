package com.ege.passkeytpm.core.repository;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;
import com.ege.passkeytpm.core.impl.pojo.UserSessionImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSessionImpl, Long> {
    List<UserSessionImpl> findByExpiresAtBeforeAndIsValidTrue(LocalDateTime time);
    UserSessionImpl findBySessionId(String sessionId);
    List<UserSessionImpl> findByUserAndExpiresAtBeforeAndIsValidTrue(UserImpl user, LocalDateTime time);
}
