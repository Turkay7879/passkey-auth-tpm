package com.ege.passkeytpm.core.impl.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ege.passkeytpm.core.api.AuthenticationService;
import com.ege.passkeytpm.core.api.SecurityManagerService;
import com.ege.passkeytpm.core.api.UserService;
import com.ege.passkeytpm.core.impl.pojo.UserImpl;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private SecurityManagerService securityManagerService;

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    
    public String authUserWithPassword(UserImpl user) throws Exception {
        if (user == null || !StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("User to authenticate is null or has missing credentials");
        }

        UserImpl user4Auth = null;
        if (user.getDbId() != null) { user4Auth = userService.searchUserByDbId(user.getDbId()); }
        else if (StringUtils.hasText(user.getId())) { user4Auth = userService.searchUserById(user.getId()); }
        else if (StringUtils.hasText(user.getMail())) { 
            List<UserImpl> result = userService.search(user);
            if (!result.isEmpty()) {
                user4Auth = result.get(0);
            }
        }

        if (user4Auth == null) {
            logger.error("Cannot find user with following " + (user.getDbId() != null ? "dbId: {}" : "id: {}"), user.getDbId() != null ? user.getDbId() : user.getId());
            throw new Exception("Could not find the user to authenticate");
        }

        char[] password = user.getPassword().toCharArray();
        boolean result = securityManagerService.verifyPassword(user4Auth.getPassword(), user4Auth.getSalt(), password);
        return result ? "OK" : "FAIL";
    }
}