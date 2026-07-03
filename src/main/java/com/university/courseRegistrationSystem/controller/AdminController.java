package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.CourseRequest;
import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.CourseUpdateRequest;
import com.university.courseRegistrationSystem.dto.StudentSummaryResponse;
import com.university.courseRegistrationSystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor


public class AdminController{
    private final AdminService adminService;

    // POST /admin/courses
    // admin adds a new course to the system
    @PostMapping("/course")
    public ResponseEntity<String> addCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addCourse(request));
    }

    @DeleteMapping("/course/{code}")
    public ResponseEntity<String> deleteCourse(@PathVariable String code) {
        return ResponseEntity.ok(adminService.deleteCourse(code));
    }

    // PUT /admin/courses/{code}/seats
    // admin updates total seat count for a course
    @PatchMapping("/course/{code}")
    public ResponseEntity<String> updateCourse(@PathVariable String code, @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateCourse(code, request));
    }


    // GET /admin/courses
    // admin views all courses in the system
    @GetMapping("/course")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // GET /admin/students
    // admin views all registered students
    @GetMapping("/student")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }
}
