package com.university.courseRegistrationSystem.repository;

import com.university.courseRegistrationSystem.model.Grade;
import com.university.courseRegistrationSystem.model.LetterGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade,Long> {
    // to get all grade of a student for specific semester
    List<Grade> findByStudentIdAndSemester(Long studentId, String semester);

    // to get all semester grade for a particular student
    List<Grade> findByStudentId(Long studentId);

    // check if grade already exists before assign
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    // to get specific subject grade for student
    Optional<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Modifying
    @Transactional
    @Query("UPDATE Grade g SET g.letterGrade = :letterGrade WHERE g.student.id = :studentId AND g.course.id = :courseId")
    void assignGrade(@Param("studentId") Long studentId,@Param("courseId") Long courseId,@Param("letterGrade") LetterGrade letterGrade);
}