package com.ege.passkeytpm.core.impl.job;

import com.ege.passkeytpm.core.impl.pojo.UserPasskeyAuthImpl;
import com.ege.passkeytpm.core.repository.UserPasskeyAuthRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PasskeyAuthSessionExpireJob implements Job {
    @Autowired
    private UserPasskeyAuthRepository authRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        logger.info("Initializing {}", this.getClass().getSimpleName());
    }

    @PreDestroy
    public void destroy() {
        logger.info("Destroying {}", this.getClass().getSimpleName());
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int expiredAuthChallenge = 0;

        LocalDateTime now = LocalDateTime.now();
        List<UserPasskeyAuthImpl> expiredChallenges = authRepository.findByExpiresAtBeforeAndIsValidTrue(now);
        if (expiredChallenges != null && !expiredChallenges.isEmpty()) {
            for (UserPasskeyAuthImpl auth : expiredChallenges) {
                auth.setIsValid(false);
                authRepository.save(auth);
                expiredAuthChallenge++;
            }
        }

        if (expiredAuthChallenge > 0) {
            logger.info("Expired auth challenge: {}", expiredAuthChallenge);
        }
        logger.info("Finished {} job", this.getClass().getSimpleName());
    }
}
