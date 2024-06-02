package com.ege.passkeytpm.core.impl.service;

import com.ege.passkeytpm.core.api.SecurityManagerService;
import com.ege.passkeytpm.core.api.SessionManagerService;
import com.ege.passkeytpm.core.api.UserService;
import com.ege.passkeytpm.core.api.exception.InvalidSessionException;
import com.ege.passkeytpm.core.api.exception.MissingCredentialsException;
import com.ege.passkeytpm.core.api.exception.MissingSessionException;
import com.ege.passkeytpm.core.impl.pojo.UserImpl;
import com.ege.passkeytpm.core.impl.pojo.UserSessionImpl;
import com.ege.passkeytpm.core.repository.UserSessionRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionManagerServiceImpl implements SessionManagerService {
    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private SecurityManagerService securityManagerService;

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @PostConstruct
    public void init() {
        logger.info("Initialized {} service", this.getClass().getSimpleName());
    }

    @Override
    public void createSession(UserImpl user) throws Exception {
        if (user == null || user.getDbId() == null)
            throw new Exception("User cannot be null to create a session!");

        LocalDateTime now = LocalDateTime.now();
        UserSessionImpl session = new UserSessionImpl();
        session.setUser(user);
        session.setSessionId(securityManagerService.generateNonce());
        session.setCreatedAt(now);
        session.setExpiresAt(now.plusMinutes(DEFAULT_SESSION_LENGTH_MINS));
        session.setValid(true);
        sessionRepository.save(session);
    }

    @Override
    public boolean isSessionValid(String sessionId) throws Exception {
        UserSessionImpl session = getSession(sessionId);
        if (session.getExpiresAt() == null)
            throw new Exception("Session cannot have an empty expiration date!");

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(session.getExpiresAt());
    }

    @Override
    public void invalidateSession(String sessionId) throws Exception {
        UserSessionImpl session = getSession(sessionId);
        session.setValid(false);
        sessionRepository.save(session);
    }

    @Override
    public UserSessionImpl findSessionOfUser(UserImpl user2Search) throws Exception {
        List<UserImpl> userList = userService.search(user2Search, true);
        if (userList == null || userList.size() != 1) {
            throw new MissingCredentialsException();
        }

        UserImpl user = userList.get(0);
        LocalDateTime now = LocalDateTime.now();
        List<UserSessionImpl> sessionList = sessionRepository.findByUserAndExpiresAtBeforeAndIsValidTrue(user, now);
        if (sessionList == null || sessionList.size() != 1) {
            throw new MissingSessionException();
        }

        return sessionList.get(0);
    }

    private UserSessionImpl getSession(String sessionId) throws Exception {
        if (!StringUtils.hasText(sessionId))
            throw new Exception("Session id cannot be null or empty!");

        UserSessionImpl session = sessionRepository.findBySessionId(sessionId);
        if (session == null)
            throw new InvalidSessionException();

        return session;
    }
}
