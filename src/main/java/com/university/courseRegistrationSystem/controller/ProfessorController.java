package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.GradeRequest;
import com.university.courseRegistrationSystem.dto.StudentEnrollmentResponse;
import com.university.courseRegistrationSystem.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/professors")
@RequiredArgsConstructor

public class ProfessorController {
    private final ProfessorService professorService;

    // GET professors/courses
    // professor views all courses assigned to them
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourse(){
        return ResponseEntity.ok(professorService.getMyCourses());
    }

    // GET professors/courses/{course-code}/students
    // professor views all active students in their course
    @GetMapping("/courses/{course-code}/students")
    public ResponseEntity<List<StudentEnrollmentResponse>> getEnrolledStudents(@PathVariable("course-code") String courseCode){
        return ResponseEntity.ok(professorService.getEnrolledStudents(courseCode));
    }

    // POST professors/grades
    // professor assigns and updates grade for student
    @PostMapping("/grades")
    public ResponseEntity<String> gradeStudent(
            @RequestBody GradeRequest request) {
        professorService.gradeStudent(request);
        return ResponseEntity.ok("Grade assigned successfully");
    }

    // PATCH professors/courses/{course-code}/cgpa-criteria/{minCgpaRequired}
    // professor updates minimum CGPA required for their course

    @PatchMapping("/courses/{course-code}/cgpa-criteria/{minCgpaRequired}")
    public ResponseEntity<String> updateCgpaCriteria(
            @PathVariable("course-code") String courseCode,
            @PathVariable BigDecimal minCgpaRequired) {
        professorService.updateCgpaCriteria(courseCode, minCgpaRequired);
        return ResponseEntity.ok("CGPA criteria updated successfully");
    }

}