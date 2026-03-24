package com.university.courseRegistrationSystem.repository;

import com.university.courseRegistrationSystem.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    // Login with E-mail
    Optional<Admin> findByEmail(String email);

    // To check whether student with give email exists
    boolean existsByEmail(String email);
}
