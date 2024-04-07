package com.ege.passkeytpm.core.api;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;

public interface AuthenticationService {
    String authUserWithPassword(UserImpl user) throws Exception;
}
