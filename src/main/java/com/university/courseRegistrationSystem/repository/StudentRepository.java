package com.university.courseRegistrationSystem.repository;
import com.university.courseRegistrationSystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student,Long>{
    //Login with Email
    Optional<Student> findByEmail(String email);

    // To check whether Student which given email is exist or not
    boolean existsByEmail(String email);
}
