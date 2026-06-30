package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.EnrollmentResponse;
import com.university.courseRegistrationSystem.exception.CustomException;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Enrollment;
import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.EnrollmentRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    private Student getCurrentStudent(){

        Long studentId = Long.parseLong(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return studentRepository.findById(studentId).orElseThrow(() -> new CustomException(400,"Student with id " + studentId + " not found"));

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

        //check student semester and semester for course
        if (course.getSemester() != student.getSemester()) {
            throw new CustomException(400, "Course belongs to semester " + course.getSemester() + " but student is in semester " + student.getSemester());
        }

        try {
            course.setAvailableSeats(course.getAvailableSeats() - 1);
            courseRepository.save(course);

            Enrollment enrollment = new Enrollment(student, course);
            Enrollment saved = enrollmentRepository.save(enrollment);

            return "You Enroll successfully: " + saved.getId();

        }
        catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomException(503,"Too many simultaneous registrations. Please try again.");
        }
    }

    @Transactional
    public String dropCourse(String courseCode) {

        Student student = getCurrentStudent();

        // check  does course exist
        Course course = courseRepository.findByCode(courseCode).orElseThrow(() -> new CustomException(400,"Course not found with id: " + courseCode));

        // check  student enrolled in this course
        Enrollment enrollment = enrollmentRepository.findByCodeAndStudentId(courseCode,student.getId()).orElseThrow(() -> new CustomException(400,"You are not enrolled in: " + course.getName()));

        // check  enrollment active
        if (!enrollment.isActive()) {
            throw new CustomException(400,"You have already dropped: " + course.getName());
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
