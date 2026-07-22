package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.*;
import com.university.courseRegistrationSystem.exception.CustomException;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Professor;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.ProfessorRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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
    public String addCourse(CourseRequest request) {

        if(courseRepository.existsByCode(request.getCode())){
            throw new CustomException(409,"Course with code " + request.getCode() + " already exists");
        }

        Professor professor = professorRepository.findByEmail(request.getProfessorEmail()).orElseThrow(() -> new RuntimeException("Professor not found with email : " + request.getProfessorEmail()));

        Course course = getCourse(request, professor);

        courseRepository.save(course);

        return "Course added successfully";

    }

    private static @NonNull Course getCourse(CourseRequest request, Professor professor) {
        Course course = new Course();
        course.setCode(request.getCode());
        course.setName(request.getName());
        course.setTotalSeats(request.getTotalSeats());
        course.setProfessor(professor);
        course.setAvailableSeats(request.getTotalSeats());
        course.setCoreFlag(request.isCoreFlag());
        course.setCreditHours(request.getCreditHours());
        course.setMinCgpaRequired(request.getMinCgpaRequired());
        course.setSemester(request.getSemester());
        course.setYear(request.getYear());
        return course;
    }

    @Transactional
    public String deleteCourse(String code) {

        // check course exists
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Course not found with code: " + code));

        // just delete — CascadeType.ALL on enrollments and grades
        // will automatically delete related enrollment and grade records
        courseRepository.delete(course);
        return "Course deleted successfully";
    }

    @Transactional
    public String updateCourse(String code, CourseUpdateRequest request){
        String message = "";
        if(request.getSeats() != null){
            message += (this.updateSeatMatrix(code,request.getSeats()) + ", ");

        }

        if(request.getIsCoreFlag() != null){
            message += (this.updateCoreStatus(code,request.getIsCoreFlag()) + ", ");
        }

        if(request.getCreditHours() != null){
            message += (this.updateCreditHours(code,request.getCreditHours()) + ", ");
        }

        if(request.getProfessorEmail() != null){
            message += (this.updateProfessorForCourse(code, request.getProfessorEmail()));
        }

        if(message.isEmpty()){
            message += "No changes are made";
        }

        return message;

    }
    @Transactional
    public String updateSeatMatrix(String code, int seats) {

        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new CustomException(400,"Course not found"));

        int enrolledCount = course.getTotalSeats() - course.getAvailableSeats();

        if (seats < enrolledCount) {
            throw new CustomException(400,"Cannot reduce seats below enrolled count");
        }

        int newAvailableSeats = seats - enrolledCount;

        course.setAvailableSeats(newAvailableSeats);
        course.setTotalSeats(seats);

        courseRepository.save(course);

        return "Seats updated successfully for course code: " + code;
    }

    @Transactional
    public String updateProfessorForCourse(String code,String professorEmail) {

        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found with code :" + code));
        Professor professor = professorRepository.findByEmail(professorEmail).orElseThrow(() -> new CustomException(400,"Professor not found with id : " + professorEmail));
        course.setProfessor(professor);
        courseRepository.save(course);
        return "Professor updated successfully for course code : " + course.getCode();

    }

    @Transactional
    public String  updateCoreStatus(String code, boolean isCoreFlag) {

        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found: " + code));
        course.setCoreFlag(isCoreFlag);
        courseRepository.save(course);

        return "Course status updated successfully";

    }

    @Transactional
    public String updateCreditHours(String code,int  creditHours) {

        if (creditHours <= 0) {
            throw new CustomException(400,"Credit hours must be greater than 0");
        }
        Course course = courseRepository.findByCode(code).orElseThrow(() -> new CustomException(400,"Course not found: " + code));
        course.setCreditHours(creditHours);
        courseRepository.save(course);
        return "Credit hours updated successfully";

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
