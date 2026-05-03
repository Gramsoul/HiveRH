package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "complaint")
public class ComplaintEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_complaint;

    @Column (name = "tittle", nullable = false, length = 100)
    private String tittle;

    @Column(name = "description", nullable = false, length = 100)
    private String  description;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "reviewed")
    private Boolean reviewed;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;
}
