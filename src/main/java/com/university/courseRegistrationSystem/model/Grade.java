package com.university.courseRegistrationSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "grades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LetterGrade letterGrade;

    // gradePoints is derived from letterGrade
    // A=4.0, B=3.0, C=2.0, D=1.0, F=0.0

    @Column(precision = 3, scale = 2, nullable = false)
    private BigDecimal gradePoints;


    @Column(nullable = false)
    private String semester;

    // Tracks exactly when the professor submitted this grade
    @Column(nullable = false)
    private LocalDateTime gradedAt;

    // Before storing grade in DB it will convert letterGrade into gradePoints
    // and record grade submission time.
    @PrePersist
    public void prePersist() {
        this.gradedAt = LocalDateTime.now();
        this.gradePoints = calculateGradePoints(this.letterGrade);
    }


    // Private helper converts letter grade to grade points
    // Called by both prePersist and preUpdate
    private BigDecimal calculateGradePoints(LetterGrade grade) {
        if (grade == null) return BigDecimal.ZERO;
        switch (grade) {
            case A: return new BigDecimal("4.0");
            case B: return new BigDecimal("3.0");
            case C: return new BigDecimal("2.0");
            case D: return new BigDecimal("1.0");
            case F: return new BigDecimal("0.0");
            default: return BigDecimal.ZERO;
        }
    }
}