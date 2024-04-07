package com.ege.passkeytpm.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ege.passkeytpm.core.impl.pojo.UserImpl;

@Repository
public interface UserRepository extends JpaRepository<UserImpl, Long> {
    
}
