package com.ege.passkeytpm.core.api;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;
import com.ege.passkeytpm.core.impl.pojo.UserSessionImpl;

public interface SessionManagerService {
    Long DEFAULT_SESSION_LENGTH_MINS = 5L;

    void createSession(UserImpl user) throws Exception;
    boolean isSessionValid(String sessionId) throws Exception;
    void invalidateSession(String sessionId) throws Exception;
    UserSessionImpl findSessionOfUser(UserImpl user) throws Exception;
}
