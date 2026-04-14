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
@RequestMapping("/professor")
@RequiredArgsConstructor

public class ProfessorController {
    private final ProfessorService professorService;

    // GET professor/courses
    // professor views all courses assigned to them
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getMyCourse(){
        return ResponseEntity.ok(professorService.getMyCourses());
    }

    // GET professor/courses/{courseCode}/students
    // professor views all active students in their course
    @GetMapping("/courses/{courseCode}/students")
    public ResponseEntity<List<StudentEnrollmentResponse>> getEnrolledStudents(@PathVariable String courseCode){
        return ResponseEntity.ok(professorService.getEnrolledStudents(courseCode));
    }

    // POST professor/grades
    // professor assigns and updates grade for student
    @PostMapping("/grades")
    public ResponseEntity<String> gradeStudent(
            @RequestBody GradeRequest request) {
        professorService.gradeStudent(request);
        return ResponseEntity.ok("Grade assigned successfully");
    }

    // PUT professor/courses/{courseCode}/cgpa-criteria
    // professor updates minimum CGPA required for their course
    @PutMapping("/courses/{courseCode}/cgpa-criteria/{minCgpaRequired}")
    public ResponseEntity<String> updateCgpaCriteria(
            @PathVariable String courseCode,
            @PathVariable BigDecimal minCgpaRequired) {
        professorService.updateCgpaCriteria(courseCode, minCgpaRequired);
        return ResponseEntity.ok("CGPA criteria updated successfully");
    }

}