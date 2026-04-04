package com.university.courseRegistrationSystem.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    @Id
    @Column(name = "professor_id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String Name;

    @Column(nullable = false,unique = true)
    private String Email;

    @Column(nullable = false)
    private String Password;

    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();
}
