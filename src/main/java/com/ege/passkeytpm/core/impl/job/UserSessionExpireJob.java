package com.ege.passkeytpm.core.impl.job;

import com.ege.passkeytpm.core.api.SessionManagerService;
import com.ege.passkeytpm.core.impl.pojo.UserSessionImpl;
import com.ege.passkeytpm.core.repository.UserSessionRepository;
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
public class UserSessionExpireJob implements Job {
    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private SessionManagerService sessionManagerService;

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
        int expiredSessionCount = 0;

        try {
            LocalDateTime now = LocalDateTime.now();
            List<UserSessionImpl> sessionList = sessionRepository.findByExpiresAtBeforeAndIsValidTrue(now);
            if (sessionList != null && !sessionList.isEmpty()) {
                for (UserSessionImpl session : sessionList) {
                    sessionManagerService.invalidateSession(session.getSessionId());
                    expiredSessionCount++;
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while checking and invalidating expired user sessions", e);
            throw new JobExecutionException(e);
        }

        if (expiredSessionCount > 0)
            logger.info("Invalidated {} expired user sessions", expiredSessionCount);
        logger.info("Finished {}", this.getClass().getSimpleName());
    }
}
