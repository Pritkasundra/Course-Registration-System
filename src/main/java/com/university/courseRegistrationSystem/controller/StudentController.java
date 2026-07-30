package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.*;
import com.university.courseRegistrationSystem.service.GradeService;
import com.university.courseRegistrationSystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor


public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    // GET /students/profile
    // Students view their own profile
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getProfile(){
        return ResponseEntity.ok(studentService.getProfile());
    }

    // GET /students/courses/eligible
    // Student see list of all eligible course
    @GetMapping("/courses/eligible")
    public ResponseEntity<List<CourseResponse>> getEligibleCourses(){
        return ResponseEntity.ok(studentService.getEligibleCourses());
    }

    // POST /students/enrollments
    // student registers for a course
    @PostMapping("/enrollments")
    public ResponseEntity<String> enrollCourse(@RequestBody EnrollmentRequest enrollmentRequest){
        return ResponseEntity.ok(studentService.enrollCourse(enrollmentRequest.getCourseCode()));
    }

    // GET /students/enrollments
    // student views all their currently active enrollments
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getRegisteredCourses() {
        return ResponseEntity.ok(studentService.getRegisteredCourses());
    }

    // DELETE /students/enrollments
    // student drops a course
    @DeleteMapping("/enrollments")
    public ResponseEntity<String> dropCourse(@RequestBody EnrollmentRequest enrollmentRequest) {

        return ResponseEntity.ok(studentService.dropCourse(enrollmentRequest.getCourseCode()));
    }

    // GET /students/grades
    // student views all grades across all semesters
    @GetMapping("/grades")
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    // GET /student/gardes/semester
    // student views all grades for a specific semester
    @GetMapping("/grades/{semester}")
    public ResponseEntity<List<GradeResponse>> getGradesBySemester(
            @PathVariable String semester) {
        return ResponseEntity.ok(gradeService.getSemesterGrade(semester));
    }
}
