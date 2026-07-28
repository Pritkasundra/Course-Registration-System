package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.CourseRequest;
import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.CourseUpdateRequest;
import com.university.courseRegistrationSystem.dto.StudentSummaryResponse;
import com.university.courseRegistrationSystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor


public class AdminController{
    private final AdminService adminService;

    // POST /admins/courses
    // admin adds a new course to the system
    @PostMapping("/courses")
    public ResponseEntity<String> addCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addCourse(request));
    }

    @DeleteMapping("/courses/{course-code}")
    public ResponseEntity<String> deleteCourse(@PathVariable("course-code") String courseCode) {
        return ResponseEntity.ok(adminService.deleteCourse(courseCode));
    }

    // Patch /admins/courses/{code}
    // admin updates Course
    @PatchMapping("/courses/{course-code}")
    public ResponseEntity<String> updateCourse(@PathVariable("course-code") String courseCode, @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateCourse(courseCode, request));
    }


    // GET /admins/courses
    // admin views all courses in the system
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // GET /admins/students
    // admin views all registered students
    @GetMapping("/students")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }
}
