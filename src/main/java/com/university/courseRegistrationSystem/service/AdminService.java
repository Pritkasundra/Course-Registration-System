package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.*;
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
            throw new RuntimeException("Course with code " + request.getCode() + " already exists");
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
    public ResponseEntity<String> updateSeatMatrix(UpdateSeatMatrixRequest request) {

        Course course = courseRepository.findByCode(request.getCode()).orElseThrow(() -> new RuntimeException("Course no found with code :" + request.getCode()));

        int enrolledCount = course.getTotalSeats() - course.getAvailableSeats();

        if (request.getNewTotalSeats() < enrolledCount) {
            throw new RuntimeException("Cannot reduce seats to " + request.getNewTotalSeats() + ". Already " + enrolledCount + " students enrolled");
        }
        int newAvailableSeats = request.getNewTotalSeats() - enrolledCount;

        course.setAvailableSeats(newAvailableSeats);
        course.setTotalSeats(request.getNewTotalSeats());

        courseRepository.save(course);

        return ResponseEntity.ok("Seats updated successfully for course code : " + course.getCode());
    }

    @Transactional
    public ResponseEntity<String> updateProfessorForCourse(UpdateProfessorForCourseRequest request) {

        Course course = courseRepository.findByCode(request.getCode()).orElseThrow(() -> new RuntimeException("Course no found with code :" + request.getCode()));
        Professor professor = professorRepository.findByEmail(request.getProfessorEmail()).orElseThrow(() -> new RuntimeException("Professor not found with email : " + request.getProfessorEmail()));
        course.setProfessor(professor);
        courseRepository.save(course);
        return ResponseEntity.ok("Professor updated successfully for course code : " + course.getCode());

    }

    @Transactional
    public ResponseEntity<String>  updateCoreStatus(UpdateCoreStatusRequest request) {

        Course course = courseRepository.findByCode(request.getCode()).orElseThrow(() -> new RuntimeException("Course not found: " + request.getCode()));
        course.setCoreFlag(request.isCoreFlag());
        courseRepository.save(course);

        return ResponseEntity.ok("Course updated successfully");

    }

    @Transactional
    public ResponseEntity<String> updateCreditHours(UpdateCreditHoursRequest request) {

        if (request.getCreditHours() <= 0) {
            throw new RuntimeException("Credit hours must be greater than 0");
        }
        Course course = courseRepository.findByCode(request.getCode()).orElseThrow(() -> new RuntimeException("Course not found: " + request.getCode()));
        course.setCreditHours(request.getCreditHours());
        courseRepository.save(course);
        return ResponseEntity.ok("Credit hours updated successfully");

    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<StudentSummaryResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(s -> new StudentSummaryResponse(s.getId(), s.getName(), s.getEmail(), s.getCgpa(), s.getSemester(), s.getYear())).collect(Collectors.toList());
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
                        : null
        );
    }
}
