package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.StudentProfileResponse;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService{

    public final StudentRepository studentRepository;
    public final CourseRepository courseRepository;

    //Get CurrentStudent
    private Student getCurrentStudent(){

        Long studentId = Long.parseLong(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student with id " + studentId + " not found"));

    }

    // View Student profile
    public StudentProfileResponse getProfile(){

        Student student = getCurrentStudent();

        int remaining = student.getTotalRequiredCredits() - student.getCompletedCredits();

        return new StudentProfileResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getSemester(),
                student.getYear(),
                student.getCgpa(),
                student.getCompletedCredits(),
                student.getTotalRequiredCredits(),
                remaining
        );
    }
    public List<CourseResponse> getEligibleCourses(){

        Student student = getCurrentStudent();

        // if student has no CGPA yet treat as 0.0
        BigDecimal cgpa = student.getCgpa() != null ? student.getCgpa() : BigDecimal.ZERO;
        String semester = student.getSemester() != null ? student.getSemester() : null;
        int year = student.getYear();

        return courseRepository.findEligibleCourse(cgpa,semester,year)
                .stream()
                .map(course -> new CourseResponse(
                        course.getId(),
                        course.getName(),
                        course.getCode(),
                        course.getTotalSeats(),
                        course.getAvailableSeats(),
                        course.getCreditHours(),
                        course.isCoreFlag(),
                        course.getMinCgpaRequired(),
                        course.getProfessor() != null
                                ? course.getProfessor().getId()
                                : null,
                        course.getSemester(),
                        course.getYear()
                ))
                .collect(Collectors.toList());
    }
}
