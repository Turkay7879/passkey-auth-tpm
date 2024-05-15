package com.ege.passkeytpm.core.api;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;

public interface AuthenticationService {
    String authUserWithPassword(UserImpl user) throws Exception;
    String authUserWithPasskey(UserImpl user, String data, String digest) throws Exception;
    String generateChallenge4User(UserImpl user) throws Exception;
}
