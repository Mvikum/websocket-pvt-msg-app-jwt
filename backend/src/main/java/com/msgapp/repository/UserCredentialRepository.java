package com.msgapp.repository;

import com.msgapp.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

    boolean existsByEmail(String email);

    UserCredential findByEmail(String name);

    UserCredential findByUserName(String name);
}
