package com.university.courseRegistrationSystem.controller;

import com.university.courseRegistrationSystem.dto.EnrollmentRequest;
import com.university.courseRegistrationSystem.dto.EnrollmentResponse;
import com.university.courseRegistrationSystem.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {


    private EnrollmentService enrollmentService;

    @PostMapping("/register")
    public EnrollmentResponse register(@RequestBody EnrollmentRequest request) {
        return enrollmentService.registerForCourse(request);
    }

    @PostMapping("/drop")
    public EnrollmentResponse drop(@RequestParam Long studentId, @RequestParam Long courseId) {
        return enrollmentService.dropCourse(studentId, courseId);
    }

    @GetMapping("/{studentId}")
    public List<EnrollmentResponse> getCourses(@PathVariable Long studentId) {
        return enrollmentService.getRegisteredCoures(studentId);
    }
}