package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.GradeResponse;
import com.university.courseRegistrationSystem.model.Grade;
import com.university.courseRegistrationSystem.model.Student;
import com.university.courseRegistrationSystem.repository.GradeRepository;
import com.university.courseRegistrationSystem.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class GradeService {

    private StudentRepository studentRepository;
    private GradeRepository gradeRepository;

    // Get currently logged in student
    private Student getCurrentStudent(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return studentRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("Student not found"));
    }

    // View all grade
    public List<GradeResponse> getAllGrades(){
        Student student = getCurrentStudent();

        List<Grade> grades = gradeRepository.findByStudentId(student.getId());

        return grades.stream().map(this::mapToResponse).collect(Collectors.toList());

    }

    // View all grades by semester
    public List<GradeResponse> getSemesterGrade(String semester){
        Student student = getCurrentStudent();
        List<Grade> grades = gradeRepository.findByStudentIdAndSemester(student.getId(),semester);

        if(grades.isEmpty()){
            throw new RuntimeException("Grades not found for semester: " + semester);
        }

        return grades.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private GradeResponse mapToResponse(Grade grade) {
        return new GradeResponse(
                grade.getCourse().getId(),
                grade.getCourse().getName(),
                grade.getCourse().getCode(),
                grade.getCourse().getCreditHours(),
                grade.getLetterGrade(),
                grade.getGradePoints(),
                grade.getSemester()
        );
    }

}
