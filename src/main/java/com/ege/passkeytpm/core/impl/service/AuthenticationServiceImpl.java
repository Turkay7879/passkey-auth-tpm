package com.ege.passkeytpm.core.impl.service;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ege.passkeytpm.core.api.SessionManagerService;
import com.ege.passkeytpm.core.api.exception.MissingCredentialsException;
import com.ege.passkeytpm.core.api.exception.NoPasskeyRegisteredException;
import com.ege.passkeytpm.core.api.exception.SessionAlreadyExistsException;
import com.ege.passkeytpm.core.impl.pojo.UserPasskeyAuthImpl;
import com.ege.passkeytpm.core.impl.pojo.UserPasskeyImpl;
import com.ege.passkeytpm.core.impl.pojo.UserSessionImpl;
import com.ege.passkeytpm.core.impl.util.ObjectFactory;
import com.ege.passkeytpm.core.repository.UserPasskeyAuthRepository;
import com.ege.passkeytpm.core.repository.UserSessionRepository;
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

    @Autowired
    private SessionManagerService sessionManagerService;

    @Autowired
    private UserPasskeyAuthRepository passkeyAuthRepository;

    @Autowired
    private UserSessionRepository sessionRepository;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private UserImpl getUser4Auth(UserImpl user2Search) throws Exception {
        UserImpl user4Auth = null;
        if (user2Search.getDbId() != null) { user4Auth = userService.searchUserByDbId(user2Search.getDbId()); }
        else if (StringUtils.hasText(user2Search.getId())) { user4Auth = userService.searchUserById(user2Search.getId()); }
        else if (StringUtils.hasText(user2Search.getMail())) {
            List<UserImpl> result = userService.search(user2Search);
            if (!result.isEmpty()) {
                user4Auth = result.get(0);
            }
        }

        if (user4Auth == null) {
            logger.error("Cannot find user with following " + (user2Search.getDbId() != null ? "dbId: {}" : "id: {}"), user2Search.getDbId() != null ? user2Search.getDbId() : user2Search.getId());
            throw new Exception("Could not find the user to authenticate");
        }

        return user4Auth;
    }

    public String authUserWithPassword(UserImpl user) throws Exception {
        if (user == null || !StringUtils.hasText(user.getPassword())) {
            throw new MissingCredentialsException();
        }

        UserImpl user4Auth = getUser4Auth(user);
        char[] password = user.getPassword().toCharArray();
        boolean result = securityManagerService.verifyPassword(user4Auth.getPassword(), user4Auth.getSalt(), password);
        LocalDateTime now = LocalDateTime.now();
        List<UserSessionImpl> existingSessions = sessionRepository.findByUserAndExpiresAtAfterAndIsValidTrue(user4Auth, now);
        if (result && existingSessions != null && !existingSessions.isEmpty()) {
            throw new SessionAlreadyExistsException();
        } else if (result) {
            sessionManagerService.createSession(user4Auth);
        }
        return result ? "OK" : "FAIL";
    }

    @Override
    public String authUserWithPasskey(UserImpl user, String data, String digest) throws Exception {
        if (user == null || !StringUtils.hasText(data) || !StringUtils.hasText(digest)) {
            throw new MissingCredentialsException();
        }

        UserImpl user4Auth = getUser4Auth(user);
        List<UserPasskeyImpl> passkeysInDB = new ArrayList<>(user4Auth.getPasskeys());
        UserPasskeyImpl passkey4Auth = passkeysInDB.get(0);

        PublicKey publicKey = ObjectFactory.getInstance().model2Bean(securityManagerService.decrypt(passkey4Auth.getPublicKey()));
        byte[] signature = ObjectFactory.getInstance().hexString2ByteArray(digest);
        boolean authResult = securityManagerService.verifySignature(publicKey, data.getBytes(StandardCharsets.UTF_8), signature);
        LocalDateTime now = LocalDateTime.now();
        List<UserSessionImpl> existingSessions = sessionRepository.findByUserAndExpiresAtAfterAndIsValidTrue(user4Auth, now);
        if (authResult && existingSessions != null && !existingSessions.isEmpty()) {
            throw new SessionAlreadyExistsException();
        } else if (authResult) {
            sessionManagerService.createSession(user4Auth);
        }
        return authResult ? "OK" : "FAIL";
    }

    @Override
    public String generateChallenge4User(UserImpl user) throws Exception {
        if (user == null || !StringUtils.hasText(user.getMail())) {
            throw new MissingCredentialsException();
        }

        UserImpl user4Auth = getUser4Auth(user);
        if (user4Auth.getPasskeys() == null || user4Auth.getPasskeys().isEmpty()) {
            throw new NoPasskeyRegisteredException();
        }

        String keyAuth = securityManagerService.decrypt((new ArrayList<>(user4Auth.getPasskeys())).get(0).getKeyAuth());
        String challenge = securityManagerService.generateNonce();
        UserPasskeyAuthImpl authSession = new UserPasskeyAuthImpl(user4Auth, challenge);
        passkeyAuthRepository.save(authSession);

        return challenge + "#" + keyAuth;
    }
}