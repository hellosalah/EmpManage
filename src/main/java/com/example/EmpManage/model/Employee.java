package com.example.EmpManage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    private String firstName;

    private String lastName;

    private String jobTitle;

    private String department;

    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    private int contactInformation;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;
}

enum EmploymentStatus {
    FULL_TIME, INTERN, FREELANCER
}

