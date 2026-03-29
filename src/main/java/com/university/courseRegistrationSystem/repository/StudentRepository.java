package com.university.courseRegistrationSystem.repository;
import com.university.courseRegistrationSystem.model.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student,Long>{
    //Login with Email
    Optional<Student> findByEmail(String email);

    // To check whether Student which given email is exist or not
    boolean existsByEmail(String email);

    // update student CGPA and completed credits after grading
    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.cgpa = :cgpa, " + "s.completedCredits = :completedCredits WHERE s.id = :studentId")
    void updateCgpaAndCredits(@Param("studentId") Long studentId, @Param("cgpa") BigDecimal cgpa, @Param("completedCredits") int completedCredits);
}
