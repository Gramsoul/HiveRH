package com.HiveGroup.HiveRH.Features.Vacation;


import com.HiveGroup.HiveRH.Features.Certificate.Certificate;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "vacation")
public class VacationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_vacation;

    @Column (name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "accepted")
    private boolean isAccepted;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "paid")
    private boolean isPaid;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;
}
