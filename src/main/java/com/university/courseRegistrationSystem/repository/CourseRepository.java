package com.university.courseRegistrationSystem.repository;

import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {

    // to check duplicate course while adding new course
    boolean existsByCode(String Code);

    // find course by code
    Optional<Course> findByCode(String code);

    // professor will use this to see their course
    List<Course> findByProfessorId(Long professorId);

    // Student will use this to see all eligible course
    @Query("SELECT c FROM Course c WHERE c.availableSeats > 0 " + "AND c.minCgpaRequired <= :cgpa " + "AND c.semester = :semester " + "AND c.year = :year")
    List<Course> findEligibleCourse(@Param("cgpa") BigDecimal cgpa, @Param("semester") String semester, @Param("year") int year);

    // this will be used to change course cgpa criteria
    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.minCgpaRequired = :cgpa WHERE c.code = :code")
    void updateCgpaCriteria(@Param("code") String code,@Param("cgpa") BigDecimal cgpa);

    // this will be used to change total number of seat for any course
    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.totalSeats = :seats WHERE c.code = :code")
    void updateSeatMatrix(@Param("code") String code,@Param("seats") int seats);

    // this will be used to change core course status
    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.isCoreFlag = :isCore WHERE c.code = :code")
    void updateCoreStatus(@Param("code") String code,@Param("isCore") boolean isCore);

    // this will be used to change credit hours for a specific course
    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.creditHours = :creditHours WHERE c.code = :code")
    void updateCreditHours(@Param("code") String code,@Param("creditHours") int creditHours);

    // this will be used to change professor for a specific course
    @Modifying
    @Transactional
    @Query(value = "UPDATE Course c SET c.professor = :professor WHERE c.code = :code")
    void updateProfessor(@Param("code") String code,@Param("professor") Professor professor);


}
