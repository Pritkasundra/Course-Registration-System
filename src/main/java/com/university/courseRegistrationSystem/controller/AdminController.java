package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.CourseRequest;
import com.university.courseRegistrationSystem.dto.CourseResponse;
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
@PreAuthorize("hasAuthority('ADMIN')")

public class AdminController{
    private final AdminService adminService;

    // POST /admin/courses
    // admin adds a new course to the system
    @PostMapping("/course")
    public ResponseEntity<ResponseEntity<String>> addCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addCourse(request));
    }

    @DeleteMapping("/delete-course/{code}")
    public ResponseEntity<String> deleteCourse(@PathVariable String code) {
        return ResponseEntity.ok(adminService.deleteCourse(code));
    }

    // PUT /admin/courses/{code}/seats
    // admin updates total seat count for a course
    @PutMapping("/courses/{code}/seats")
    public ResponseEntity<String> updateSeatMatrix(@PathVariable String code, @RequestParam int seats) {
        return ResponseEntity.ok(adminService.updateSeatMatrix(code, seats));
    }

    // PUT /admin/courses/{code}/professor
    // admin reassigns professor for a course
    @PutMapping("/courses/{code}/professor")
    public ResponseEntity<String> updateCourseProfessor(@PathVariable String code, @RequestParam String professorEmail) {
        return ResponseEntity.ok(adminService.updateProfessorForCourse(code, professorEmail));
    }

    // PUT /admin/courses/{code}/core-status
    // admin marks course as core or elective
    @PutMapping("/courses/{code}/core-status")
    public ResponseEntity<String> updateCoreStatus(@PathVariable String code, @RequestParam boolean isCoreFlag) {
        return ResponseEntity.ok(adminService.updateCoreStatus(code, isCoreFlag));
    }

    // PUT /admin/courses/{code}/credit-hours
    // admin updates credit hours for a course
    @PutMapping("/courses/{code}/credit-hours")
    public ResponseEntity<String> updateCreditHours(@PathVariable String code, @RequestParam int creditHours) {
        return ResponseEntity.ok(adminService.updateCreditHours(code, creditHours));
    }

    // GET /admin/courses
    // admin views all courses in the system
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    // GET /admin/students
    // admin views all registered students
    @GetMapping("/students")
    public ResponseEntity<List<StudentSummaryResponse>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }
}
