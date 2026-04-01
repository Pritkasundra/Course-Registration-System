package com.university.courseRegistrationSystem.service;


import com.university.courseRegistrationSystem.dto.EnrollmentRequest;
import com.university.courseRegistrationSystem.dto.EnrollmentResponse;
import com.university.courseRegistrationSystem.exception.*;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Enrollment;
import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.EnrollmentRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private EnrollmentRepository enrollmentRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;

    //REGISTER FOR COURSE

    //This annotation ensure that either the process success completely or fail completely
    @Transactional
    public EnrollmentResponse registerForCourse(EnrollmentRequest request){

        //check that studentId or courseId is not null
        if (request.getStudentId() == null || request.getCourseId() == null) {
            throw new IllegalArgumentException("Invalid request data");
        }

        //Check whether student is exit or not
        Student student = studentRepository.findById(request.getStudentId()).orElseThrow(()-> new StudentNotFoundException("Student Not Found with id :"+request.getStudentId()));

        //Check whether course is exit or not
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(()-> new CourseNotFoundException("Course Not Found with id :"+request.getCourseId()));

        //Check that the course is already Enrolled by student or not
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());

        //if student is already exist then throw this exception
        if (alreadyEnrolled) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        //if course is already full then throw this exception
        if (course.getAvailableSeats() <= 0) {
            throw new CourseFullException("No seats available in course: " + course.getName());
        }

        //if student not has enough CGPA for the course then throw this exception
        if (student.getCgpa().compareTo(course.getMinCgpaRequired()) < 0) {
            throw new NotEligibleException("Your CGPA " + student.getCgpa() + " does not meet the minimum requirement of " + course.getMinCgpaRequired());
        }

        //call the parametrized constrictor of Enrollment class
        Enrollment enrollment = new Enrollment(student, course);

        //Update the available seat for the course
        course.setAvailableSeats(course.getAvailableSeats() - 1);
        courseRepository.save(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToResponse(savedEnrollment);
    }

    //DROP COURSE
    public EnrollmentResponse dropCourse(Long studentId, Long courseId){

        //check that studentId or courseId is not null
        if (studentId == null || courseId == null) {
            throw new IllegalArgumentException("Invalid request data");
        }

        //check weather this student is enrolled in this course
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId,courseId).orElseThrow(()-> new EnrollmentNotFoundException("Enrollment Not Found for this Student and Course"));

        //check for course is already dropped or not
        if(!enrollment.isActive()){
            throw new EnrollmentNotFoundException("course is already dropped");
        }

        //check for course is core course or not
        if(enrollment.getCourse().isCoreFlag()){
            throw new CannotDropCoreException("core course can't be dropped : " + enrollment.getCourse().getName());
        }

        //drop this course
        enrollment.drop();

        //increase the Available seat for that course
        Course course = enrollment.getCourse();
        course.setAvailableSeats(course.getAvailableSeats() + 1);
        courseRepository.save(course);

        //save updated enrollment
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return mapToResponse(updatedEnrollment);
    }

    //VIEW REGISTERED COURSES (for student)
    public List<EnrollmentResponse> getRegisteredCoures(Long studentId){

        //check is not null
        if(studentId == null){
            throw new IllegalArgumentException("Invalid request data");
        }

        //check whether student is exit or not
        studentRepository.findById(studentId).orElseThrow(()->new StudentNotFoundException("Student Not Found with id :"+studentId));

        //take all enrollment list for particular student id
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);

        return enrollments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    //map Enrollment entity to Response DTO
    private EnrollmentResponse mapToResponse(Enrollment enrollment){
        EnrollmentResponse response = new EnrollmentResponse();
        response.setEnrollmentId(enrollment.getId());
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(enrollment.getStudent().getName());
        response.setCourseId(enrollment.getCourse().getId());
        response.setCourseName(enrollment.getCourse().getName());
        response.setCourseCode(enrollment.getCourse().getCode());
        response.setStatus(enrollment.getStatus().name());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        return response;
    }
}


