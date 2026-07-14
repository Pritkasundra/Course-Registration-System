package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.EnrollmentResponse;
import com.university.courseRegistrationSystem.dto.StudentProfileResponse;
import com.university.courseRegistrationSystem.exception.CustomException;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Enrollment;
import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.EnrollmentRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService{

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    //Get CurrentStudent
    private Student getCurrentStudent(){

        Long studentId = Long.parseLong(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return studentRepository.findById(studentId).orElseThrow(() -> new CustomException(404,"Student with id " + studentId + " not found"));

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

    public List<Course> getEligibleCourses(){

        Student student = getCurrentStudent();

        // if student has no CGPA yet treat as 0.0
        BigDecimal cgpa = student.getCgpa() != null ? student.getCgpa() : BigDecimal.ZERO;
        String semester = student.getSemester() != null ? student.getSemester() : null;
        int year = student.getYear();

        return new ArrayList<>(courseRepository.findEligibleCourse(cgpa, semester, year));
    }

    public List<EnrollmentResponse> getRegisteredCourses() {
        Student student = getCurrentStudent();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(student.getId(), EnrollmentStatus.ACTIVE);

        return enrollments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public String enrollCourse(String courseCode){

        Student student = getCurrentStudent();

        // check  does course exist
        Course course = courseRepository.findByCodeLock(courseCode).orElseThrow(() -> new CustomException(400,"Course not found with code: " + courseCode));


        // check  is student already enrolled in this course
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(student.getId(),course.getId(),EnrollmentStatus.ACTIVE)) {

            throw new CustomException(409,"You are already enrolled in: " + course.getName());
        }

        // check  does student CGPA meet minimum requirement
        if (student.getCgpa() != null && student.getCgpa().compareTo(course.getMinCgpaRequired()) < 0) {

            throw new CustomException(400,"Your CGPA " + student.getCgpa() + " does not meet minimum requirement of " + course.getMinCgpaRequired() + " for course: " + course.getName());
        }

        // check  are seats available
        if (course.getAvailableSeats() <= 0) {
            throw new CustomException(400,"No seats available in course: " + course.getName());
        }

        if (!course.getSemester().equals(student.getSemester())) {
            throw new CustomException(400, "Course belongs to semester " + course.getSemester() + " but student is in semester " + student.getSemester());
        }

        course.setAvailableSeats(course.getAvailableSeats() - 1);
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment(student, course);
        Enrollment saved = enrollmentRepository.save(enrollment);

        return "You Enroll successfully for course : " + saved.getId();

    }

    @Transactional
    public String dropCourse(String courseCode) {

        Student student = getCurrentStudent();

        // check  does course exist
        Course course = courseRepository.findByCode(courseCode).orElseThrow(() -> new CustomException(404,"Course not found with id: " + courseCode));

        // check  student enrolled in this course
        Enrollment enrollment = enrollmentRepository.findByCodeAndStudentId(courseCode,student.getId()).orElseThrow(() -> new CustomException(404,"You are not enrolled in: " + course.getName()));

        // check  enrollment active
        if (!enrollment.isActive()) {
            throw new CustomException(409,"You have already dropped: " + course.getName());
        }

        // check  is this a core course
        // core courses cannot be dropped under any circumstances
        if (course.isCoreFlag()) {
            throw new CustomException(400,course.getName() + " is a core course and cannot be dropped");
        }

        // all checks passed drop the course
        enrollment.drop();
        enrollmentRepository.save(enrollment);

        // increment available seats back
        course.setAvailableSeats(course.getAvailableSeats() + 1);
        courseRepository.save(course);

        return "Course Dropped Successfully";
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        return new EnrollmentResponse(
                course.getName(),
                course.getCode(),
                course.getCreditHours(),
                course.isCoreFlag(),
                course.getProfessor() != null
                        ? course.getProfessor().getEmail()
                        : null
        );
    }
}
