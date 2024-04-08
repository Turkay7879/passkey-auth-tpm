package com.ege.passkeytpm.api.factory;

import com.ege.passkeytpm.api.model.UserModel;
import com.ege.passkeytpm.core.impl.pojo.UserImpl;
import com.ege.passkeytpm.core.impl.pojo.UserPasskeyImpl;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public final class ObjectFactory {
    private static ObjectFactory instance;

    private ObjectFactory() {
        instance = this;
    }

    public static ObjectFactory getInstance() {
        if (instance == null) {
            synchronized (ObjectFactory.class) {
                if (instance == null) {
                    instance = new ObjectFactory();
                }
            }
        }
        return instance;
    }

    public UserImpl model2Bean(UserModel userModel) {
        UserImpl user = new UserImpl();
        if (userModel != null) {
            if (StringUtils.hasText(userModel.getId())) {
                user.setId(userModel.getId());
            }
            if (StringUtils.hasText(userModel.getUsername())) {
                user.setUserName(userModel.getUsername());
            }
            if (StringUtils.hasText(userModel.getPassword())) {
                user.setPassword(userModel.getPassword());
            }
            if (userModel.getPasskeyToAdd() != null && !userModel.getPasskeyToAdd().isEmpty()) {
                Set<UserPasskeyImpl> passkeyToAdd = new HashSet<>(userModel.getPasskeyToAdd());
                user.setPasskeys(passkeyToAdd);
            }
        }
        return user;
    }
}
