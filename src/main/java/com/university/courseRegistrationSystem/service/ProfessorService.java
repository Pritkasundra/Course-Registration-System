package com.university.courseRegistrationSystem.service;

import com.university.courseRegistrationSystem.dto.CourseResponse;
import com.university.courseRegistrationSystem.dto.GradeRequest;
import com.university.courseRegistrationSystem.dto.StudentEnrollmentResponse;
import com.university.courseRegistrationSystem.model.*;
import com.university.courseRegistrationSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;


    // Get current logged in professor
    private Professor getCurrentProfessor(){
        Long professorId = Long.parseLong(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        return professorRepository.findById(professorId).orElseThrow(() -> new RuntimeException("professor with id " + professorId + " not found"));

    }

    // View all course of logged in professor
    public List<CourseResponse> getMyCourses() {
        Professor professor = getCurrentProfessor();

        return courseRepository.findByProfessorId(professor.getId())
                .stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    // View enrolled student for course
    public List<StudentEnrollmentResponse> getEnrolledStudents(String courseCode) {

        Professor professor = getCurrentProfessor();
        // verify this course belongs to logged in professor
        // prevents professor A from viewing professor B's students
        Course course = courseRepository.findByCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new RuntimeException(
                    "You are not authorized to view students of this course");
        }

        // get only active enrollments — not dropped students
        List<Enrollment> enrollments = enrollmentRepository
                .findByCodeAndStatus(courseCode, EnrollmentStatus.ACTIVE);

        return enrollments.stream()
                .map(e -> new StudentEnrollmentResponse(
                        e.getStudent().getEmail(),
                        e.getStudent().getName(),
                        course.getName(),
                        e.getEnrollmentDate(),
                        e.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // Grade a student
    @Transactional
    public void gradeStudent(GradeRequest request){

        Course course = courseRepository.findByCode(request.getCourseCode())
                .orElseThrow(()->new RuntimeException("Course not found!"));

        // verify course belongs to this professor
        if(!course.getProfessor().getId().equals(getCurrentProfessor().getId())){
            throw new RuntimeException(
                    "You are not authorized to grade students of this course"
            );
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Student is not enrolled in this course"));

        if(!enrollment.isActive()) {
            throw new RuntimeException(
                    "Cannot grade a student who dropped this course");
        }

        boolean gradeExists = gradeRepository.existsByStudentIdAndCourseId(student.getId(), course.getId());

        if(gradeExists){
            // update existing grade
            gradeRepository.assignGrade(student.getId(), course.getId(), request.getLetterGrade());
        }
        else{
            // create new grade
            Grade grade = new Grade(student,course,request.getLetterGrade(),request.getSemester());
            gradeRepository.save(grade);

            // update student completed credit
            student.setCompletedCredits(student.getCompletedCredits() + course.getCreditHours());
            studentRepository.save(student);
        }

        // recalculate CGPA after every grade change
        recalculateCgpa(student);
    }

    @Transactional
    public void updateCgpaCriteria(String CourseCode,BigDecimal minCgpaRequired) {

        Professor professor = getCurrentProfessor();

        Course course = courseRepository.findByCode(CourseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // verify course belongs to this professor
        if (!course.getProfessor().getId().equals(professor.getId())) {
            throw new RuntimeException(
                    "You are not authorized to update this course");
        }

        // validate CGPA range
        if (minCgpaRequired.compareTo(BigDecimal.ZERO) < 0 ||
                minCgpaRequired.compareTo(new BigDecimal("4.0")) > 0) {
            throw new RuntimeException("CGPA must be between 0.0 and 4.0");
        }

        course.setMinCgpaRequired(minCgpaRequired);
        courseRepository.save(course);
    }

    private void recalculateCgpa(Student student) {

        List<Grade> allGrades = gradeRepository
                .findAllGradesByStudentId(student.getId());

        if (allGrades.isEmpty()) return;

        BigDecimal totalWeightedPoints = BigDecimal.ZERO;
        int totalCreditHours = 0;

        for (Grade grade : allGrades) {
            int credits = grade.getCourse().getCreditHours();
            BigDecimal weighted = grade.getGradePoints()
                    .multiply(new BigDecimal(credits));
            totalWeightedPoints = totalWeightedPoints.add(weighted);
            totalCreditHours += credits;
        }

        BigDecimal cgpa = totalWeightedPoints.divide(
                new BigDecimal(totalCreditHours), 2, RoundingMode.HALF_UP);

        student.setCgpa(cgpa);
        studentRepository.save(student);
    }

    private CourseResponse mapToCourseResponse(Course course) {
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