package com.ege.passkeytpm.api;

import com.ege.passkeytpm.api.factory.ObjectFactory;
import com.ege.passkeytpm.api.model.ChallengeResponseModel;
import com.ege.passkeytpm.api.model.UserModel;
import com.ege.passkeytpm.core.api.SessionManagerService;
import com.ege.passkeytpm.core.api.exception.MissingSessionException;
import com.ege.passkeytpm.core.api.exception.SessionAlreadyExistsException;
import org.springframework.web.bind.annotation.RestController;

import com.ege.passkeytpm.core.api.AuthenticationService;
import com.ege.passkeytpm.core.api.UserService;
import com.ege.passkeytpm.core.impl.pojo.UserImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;


@RestController
@RequestMapping("/userAuth")
public class UserAuthenticationRestApi {
    private final Logger logger = LoggerFactory.getLogger(UserAuthenticationRestApi.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SessionManagerService sessionManagerService;
    
    @PostMapping("/registerUser")
    public ResponseEntity<Object> registerUser(@RequestBody UserImpl user) {
        try {
            UserImpl savedUser = userService.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error with saving user", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserModel model) {
        try {
            UserImpl bean = ObjectFactory.getInstance().model2Bean(model);
            String result = model.getUsePasskeyAuth() != null && model.getUsePasskeyAuth()
                    ? authenticationService.authUserWithPasskey(bean, model.getChallenge(), model.getDigest())
                    : authenticationService.authUserWithPassword(bean);
            return new ResponseEntity<>(result, Objects.equals(result, "OK") ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
        } catch (SessionAlreadyExistsException e) {
            return new ResponseEntity<>("User already has an active session!", HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Error with saving user", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addPasskey")
    public ResponseEntity<Object> addPasskey(@RequestBody UserModel model) {
        try {
            UserImpl bean = ObjectFactory.getInstance().model2Bean(model);
            if (sessionManagerService.findSessionOfUser(bean) == null) {
                throw new MissingSessionException();
            }
            return new ResponseEntity<>(userService.assignPasskeyToUser(bean), HttpStatus.OK);
        } catch (MissingSessionException e) {
            return new ResponseEntity<>("Login is required for this operation!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error with saving user passkey", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getChallenge")
    public ResponseEntity<Object> getChallenge(@RequestBody UserModel model) {
        try {
            UserImpl bean = ObjectFactory.getInstance().model2Bean(model);
            if (sessionManagerService.findSessionOfUser(bean) == null) {
                throw new MissingSessionException();
            }
            String data = authenticationService.generateChallenge4User(bean);
            return new ResponseEntity<>(new ChallengeResponseModel(data.split("#")[0], data.split("#")[1]), HttpStatus.OK);
        } catch (MissingSessionException e) {
            return new ResponseEntity<>("Login is required for this operation!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error with getting challenge", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
