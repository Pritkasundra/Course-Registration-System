package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.EnrollmentResponse;
import com.university.courseRegistrationSystem.dto.GradeResponse;
import com.university.courseRegistrationSystem.dto.StudentProfileResponse;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.service.GradeService;
import com.university.courseRegistrationSystem.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor


public class StudentController {

    private final StudentService studentService;
    private final GradeService gradeService;

    // GET /student/profile
    // Students view their own profile
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileResponse> getProfile(){
        return ResponseEntity.ok(studentService.getProfile());
    }

    // GET /student/courses/eligible
    // Student see list of all eligible course
    @GetMapping("/courses/eligible")
    public ResponseEntity<List<Course>> getEligibleCourses(){
        return ResponseEntity.ok(studentService.getEligibleCourses());
    }

    // POST /student/enroll/{courseCode}
    // student registers for a course
    @PostMapping("/enroll/{courseCode}")
    public ResponseEntity<String> enrollCourse(@PathVariable String courseCode){
        return ResponseEntity.ok(studentService.enrollCourse(courseCode));
    }

    // GET /student/enrollments
    // student views all their currently active enrollments
    @GetMapping("/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getRegisteredCourses() {
        return ResponseEntity.ok(studentService.getRegisteredCourses());
    }

    // DELETE /student/enrollments/{courseId}
    // student drops a course
    @DeleteMapping("/drop/{courseCode}")
    public ResponseEntity<String> dropCourse(@PathVariable String courseCode) {

        return ResponseEntity.ok(studentService.dropCourse(courseCode));
    }

    // GET /student/grades
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
