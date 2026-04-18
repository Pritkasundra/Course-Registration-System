package com.university.courseRegistrationSystem.repository;

import com.university.courseRegistrationSystem.model.Enrollment;
import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {

    // check if student is already enrolled in course
    boolean existsByStudentIdAndCourseId(Long studentId,Long courseId);

    // student view their all course including dropped
    List<Enrollment> findByStudentId(Long studentId);

    // student view specific status course dropped, active
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    // We will use this when a student want to drop a course
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId,Long CourseId);

    // professor views all active students in their course
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.course.professor.id= :professorId AND e.status = 'ACTIVE'")
    List <Enrollment> findByProfessorIdAndCourseId(@Param("courseId") Long courseId , @Param("professorId") Long professorId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.code = :courseCode AND e.student.id = :studentId AND e.status = 'ACTIVE'")
    Optional<Enrollment> findByStudentIdAndCode(@Param("courseCode") String courseCode , @Param("professorId") Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.code = :courseCode AND e.status = :enrollmentStatus")
    List<Enrollment> findByCodeAndStatus(@Param("courseCode")String courseCode,@Param("enrollmentStatus")EnrollmentStatus enrollmentStatus);
}
