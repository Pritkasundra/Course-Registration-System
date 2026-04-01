package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.GradeRequest;
import com.university.courseRegistrationSystem.dto.GradeResponse;
import com.university.courseRegistrationSystem.exception.CourseNotFoundException;
import com.university.courseRegistrationSystem.exception.NotEnrolledException;
import com.university.courseRegistrationSystem.exception.StudentNotFoundException;
import com.university.courseRegistrationSystem.model.Course;
import com.university.courseRegistrationSystem.model.Grade;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.CourseRepository;
import com.university.courseRegistrationSystem.repository.EnrollmentRepository;
import com.university.courseRegistrationSystem.repository.GradeRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    //ASSIGN GRADE — Professor assigns grade
    @Transactional
    public GradeResponse assignGrade(GradeRequest request){

        //check whether student is exist or not
        Student student = studentRepository.findById(request.getStudentId()).orElseThrow(()-> new StudentNotFoundException("Student not found with id:" + request.getStudentId()));

        //check whether course is exist or not
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(()-> new CourseNotFoundException("Course not found with id:" + request.getCourseId()));

        //check student is actually enrolled in this course
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());

        if (!isEnrolled) {
            throw new NotEnrolledException("Student is not enrolled in this course");
        }

        //check if grade already exists
        boolean gradeExists = gradeRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());

        Grade savedGrade;

        //if grade is already exist the just update that grade
        if(gradeExists){

            //update the already exist grade
            gradeRepository.updateGrade(request.getStudentId(), request.getCourseId(), request.getLetterGrade());

            // fetch updated grade to return in response
            savedGrade = gradeRepository.findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId()).orElseThrow(() -> new RuntimeException("Grade not found after update"));
        }
        else{

            //call the grade constructor
            Grade grade = new Grade(student, course, request.getLetterGrade(), request.getSemester());

            //save the grade to return in response
            savedGrade = gradeRepository.save(grade);

        }

        //after updating to assigning the grade CGPA should be recalculated
        recalculateCgpa(student);

        return mapToResponse(savedGrade);

    }

    //VIEW ALL GRADES — Student views all their grades

    public List<GradeResponse> getAllGrades(Long studentId){

        //check whether student exist or not
        studentRepository.findById(studentId).orElseThrow(()->new StudentNotFoundException("Student not found with id:" + studentId));

        //take all garde for particular student
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        return grades.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    //VIEW GRADES BY SEMESTER — Student views semester wise

    public List<GradeResponse> getGradeBySemester(Long studentId, String semester){

        //check whether student exist or not
        studentRepository.findById(studentId).orElseThrow(()->new StudentNotFoundException("Student not found with id:" + studentId));

        //take all grade for particular student for particular semester
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(studentId, semester);

        return grades.stream().map(this::mapToResponse).collect(Collectors.toList());

    }

    //RECALCULATE CGPA(called after every grade assign or update)
    private void recalculateCgpa(Student student){

        //fetch all grade for particular student
        List<Grade> allGrades = gradeRepository.findAllGradesByStudentId(student.getId());

        // CGPA = sum(gradePoints × creditHours) / sum(creditHours)
        BigDecimal totalWeightedPoints = BigDecimal.ZERO;
        int totalCredits = 0;

        for (Grade grade : allGrades) {
            int creditHours = grade.getCourse().getCreditHours();
            BigDecimal gradePoints = grade.getGradePoints();

            // gradePoints × creditHours for this course
            BigDecimal weighted = gradePoints
                    .multiply(BigDecimal.valueOf(creditHours));

            totalWeightedPoints = totalWeightedPoints.add(weighted);
            totalCredits += creditHours;
        }

        BigDecimal newCgpa = BigDecimal.ZERO;
        if (totalCredits > 0) {
            newCgpa = totalWeightedPoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
        }

        // update student CGPA and completedCredits in DB
        student.setCgpa(newCgpa);
        student.setCompletedCredits(totalCredits);
        studentRepository.save(student);
    }

    //map Grade entity to GradeResponse DTO
    private GradeResponse mapToResponse(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setGradeId(grade.getId());
        response.setStudentId(grade.getStudent().getId());
        response.setStudentName(grade.getStudent().getName());
        response.setCourseId(grade.getCourse().getId());
        response.setCourseName(grade.getCourse().getName());
        response.setCourseCode(grade.getCourse().getCode());
        response.setLetterGrade(grade.getLetterGrade().name());
        response.setGradePoints(grade.getGradePoints());
        response.setSemester(grade.getSemester());
        response.setGradedAt(grade.getGradedAt());
        return response;
    }

}
