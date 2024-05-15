package com.ege.passkeytpm.core.api;

import java.util.List;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;

public interface UserService {
    UserImpl save(UserImpl user) throws Exception;
    UserImpl searchUserByDbId(Long dbId);
    UserImpl searchUserById(String id);
    List<UserImpl> search(UserImpl user);
    List<UserImpl> search(UserImpl user, boolean strictCheck);
    UserImpl assignPasskeyToUser(UserImpl user) throws Exception;
}
