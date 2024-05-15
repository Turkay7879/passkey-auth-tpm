package com.ege.passkeytpm.core.impl.service;

import java.time.LocalDateTime;
import java.util.*;

import com.ege.passkeytpm.core.impl.pojo.UserPasskeyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ege.passkeytpm.core.api.SecurityManagerService;
import com.ege.passkeytpm.core.api.UserService;
import com.ege.passkeytpm.core.impl.pojo.UserImpl;
import com.ege.passkeytpm.core.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityManagerService securityManagerService;

    @PersistenceContext
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserImpl save(UserImpl user) throws Exception {
        validateUser(user);

        UserImpl user2Check = new UserImpl();
        user2Check.setMail(user.getMail());
        List<UserImpl> resultList = search(user2Check, true);
        if (!resultList.isEmpty() && user.getDbId() == null) {
            logger.error("A user with email \"{}\" exists", user2Check.getMail());
            throw new Exception("A user with email \"%s\" exists".formatted(user2Check.getMail()));
        }

        if (user.getDbId() == null) {
            char[] passwordToProcess = user.getPassword().toCharArray();
            String[] hashAndSalt = securityManagerService.encryptPassword(passwordToProcess).split(",");
            user.setId(UUID.randomUUID().toString());
            user.setPassword(hashAndSalt[0]);
            user.setSalt(hashAndSalt[1]);
            user.setCreatedAt(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    private void validateUser(UserImpl user) throws Exception {
        if (user == null) throw new IllegalArgumentException("User to save cannot be null");
        if (user.getDbId() != null && !(StringUtils.hasText(user.getId()) && StringUtils.hasText(user.getMail()) &&
        StringUtils.hasText(user.getPassword()) && StringUtils.hasText(user.getSalt()) &&
        StringUtils.hasText(user.getUserName()) && user.getCreatedAt() != null)) {
            throw new IllegalArgumentException("User to update has missing information");
        } else if (!StringUtils.hasText(user.getPassword()) || !StringUtils.hasText(user.getUserName()) || !StringUtils.hasText(user.getMail())) {
            throw new IllegalArgumentException("User to save has missing information");
        }
    }

    @Override
    @Transactional
    public UserImpl searchUserByDbId(Long dbId) {
        if (dbId == null) { return null; }

        UserImpl result = null;
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserImpl> query = builder.createQuery(UserImpl.class);
            Root<UserImpl> root = query.from(UserImpl.class);

            query.where(builder.equal(root.get("dbId"), dbId));
            result = entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            logger.error("Error with searching user", e);
        }

        return result;
    }

    @Override
    @Transactional
    public UserImpl searchUserById(String id) {
        if (!StringUtils.hasText(id)) { return null; }

        UserImpl result = null;
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserImpl> query = builder.createQuery(UserImpl.class);
            Root<UserImpl> root = query.from(UserImpl.class);

            query.where(builder.equal(root.get("id"), id));
            result = entityManager.createQuery(query).getSingleResult();
        } catch (Exception e) {
            logger.error("Error with searching user", e);
        }

        return result;
    }

    @Override
    @Transactional
    public List<UserImpl> search(UserImpl user) {
        return search(user, false);
    }

    @Override
    @Transactional
    public List<UserImpl> search(UserImpl user, boolean strictCheck) {
        List<UserImpl> result = new ArrayList<>();
        if (user == null) { return result; }
        
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserImpl> query = builder.createQuery(UserImpl.class);
            Root<UserImpl> root = query.from(UserImpl.class);

            List<Predicate> predicates = new ArrayList<>();
            if (user.getDbId() != null || StringUtils.hasText(user.getId())) {
                predicates.add(builder.equal(root.get(user.getDbId() != null ? "dbId" : "id"), user.getDbId() != null ? user.getDbId() : user.getId()));
            } else {
                if (user.getCreatedAt() != null) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), user.getCreatedAt()));
                }
                if (StringUtils.hasText(user.getMail())) {
                    predicates.add(strictCheck
                        ? builder.equal(root.get("mail"), user.getMail())
                        : builder.like(root.get("mail"), "%" + user.getMail() + "%")
                    );
                }
                if (StringUtils.hasText(user.getUserName())) {
                    predicates.add(strictCheck
                    ? builder.equal(root.get("userName"), user.getUserName())
                    : builder.like(root.get("userName"), "%" + user.getUserName() + "%"));
                }
            }

            query.where(predicates.toArray(new Predicate[0]));
            result = entityManager.createQuery(query).getResultList();
        } catch (Exception e) {
            logger.error("Error with searching user", e);
        }

        return result;
    }

    @Override
    @Transactional
    public UserImpl assignPasskeyToUser(UserImpl user) throws Exception {
        if (user == null || !StringUtils.hasText(user.getId())) {
            throw new IllegalArgumentException("User to assign passkey cannot be null or have empty id!");
        }

        UserImpl userInDB = new UserImpl();
        userInDB.setId(user.getId());
        List<UserImpl> result = search(userInDB, true);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("No user found with id %s!".formatted(user.getId()));
        }

        Set<UserPasskeyImpl> userPasskeys = user.getPasskeys();
        if (userPasskeys == null || userPasskeys.isEmpty()) {
            throw new IllegalArgumentException("There are no passkeys provided for user to assign!");
        } else if (userPasskeys.size() > 1) {
            throw new IllegalArgumentException("Only 1 passkey can be assigned at a time!");
        }

        userInDB = result.get(0);
        UserPasskeyImpl passkey = userPasskeys.iterator().next();

        UserPasskeyImpl passkey2Save = new UserPasskeyImpl();
        passkey2Save.setUser(userInDB);
        passkey2Save.setCreatedAt(LocalDateTime.now());
        passkey2Save.setPublicKey(securityManagerService.encrypt(passkey.getPublicKey()));
        passkey2Save.setKeyAuth(securityManagerService.hash(passkey2Save.getPublicKey() + passkey2Save.getCreatedAt().toString() + userInDB.getId()));

        Set<UserPasskeyImpl> userPasskeySet = userInDB.getPasskeys();
        userPasskeySet.add(passkey2Save);
        userInDB.setPasskeys(userPasskeySet);
        save(userInDB);

        return userInDB;
    }
}
