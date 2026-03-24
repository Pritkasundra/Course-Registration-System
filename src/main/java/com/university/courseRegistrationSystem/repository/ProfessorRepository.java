package com.university.courseRegistrationSystem.repository;

import com.university.courseRegistrationSystem.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    //Logic with email
    Optional<Professor> findByEmail(String email);

    //To check whether this email is exist or not
    Boolean existsByEmail(String email);
}
