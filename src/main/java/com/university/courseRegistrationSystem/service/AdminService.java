package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.*;
import com.university.courseRegistrationSystem.exception.CustomException;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Professor;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.ProfessorRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ResponseEntity<String> addCourse(CourseRequest request) {

        if(courseRepository.existsByCode(request.getCode())){
            throw new CustomException(409,"Course with code " + request.getCode() + " already exists");
        }

        Professor professor = professorRepository.findByEmail(request.getProfessorEmail()).orElseThrow(() -> new RuntimeException("Professor not found with email : " + request.getProfessorEmail()));

        Course course = new Course();
        course.setCode(request.getCode());
        course.setName(request.getName());
        course.setTotalSeats(request.getTotalSeats());
        course.setProfessor(professor);
        course.setAvailableSeats(request.getTotalSeats());
        course.setCoreFlag(request.isCoreFlag());
        course.setCreditHours(request.getCreditHours());
        course.setMinCgpaRequired(request.getMinCgpaRequired());

        courseRepository.save(course);

        return ResponseEntity.ok("Course added successfully");

    }

    @Transactional
    public ResponseEntity<String> deleteCourse(String code) {

        // check course exists
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Course not found with code: " + code));

        // just delete — CascadeType.ALL on enrollments and grades
        // will automatically delete related enrollment and grade records
        courseRepository.delete(course);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @Transactional
    public ResponseEntity<String> updateSeatMatrix(String code, int seats) {

        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new CustomException(404,"Course not found"));

        int enrolledCount = course.getTotalSeats() - course.getAvailableSeats();

        if (seats < enrolledCount) {
            throw new CustomException(400,"Cannot reduce seats below enrolled count");
        }

        int newAvailableSeats = seats - enrolledCount;

        course.setAvailableSeats(newAvailableSeats);
        course.setTotalSeats(seats);

        courseRepository.save(course);

        return ResponseEntity.ok("Seats updated successfully for course code: " + code);
    }

    @Transactional
    public ResponseEntity<String> updateProfessorForCourse(String code,String professorEmail) {

        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found with code :" + code));
        Professor professor = professorRepository.findByEmail(professorEmail).orElseThrow(() -> new CustomException(400,"Professor not found with id : " + professorEmail));
        course.setProfessor(professor);
        courseRepository.save(course);
        return ResponseEntity.ok("Professor updated successfully for course code : " + course.getCode());

    }

    @Transactional
    public ResponseEntity<String>  updateCoreStatus(String code, boolean isCoreFlag) {

        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found: " + code));
        course.setCoreFlag(isCoreFlag);
        courseRepository.save(course);

        return ResponseEntity.ok("Course updated successfully");

    }

    @Transactional
    public ResponseEntity<String> updateCreditHours(String code,int  creditHours) {

        if (creditHours <= 0) {
            throw new CustomException(400,"Credit hours must be greater than 0");
        }
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found: " + code));
        course.setCreditHours(creditHours);
        courseRepository.save(course);
        return ResponseEntity.ok("Credit hours updated successfully");

    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<StudentSummaryResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(s -> new StudentSummaryResponse(s.getId(), s.getName(), s.getEmail())).collect(Collectors.toList());
    }

    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
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
        );
    }
}
