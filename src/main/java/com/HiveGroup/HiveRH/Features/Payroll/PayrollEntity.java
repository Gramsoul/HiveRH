package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "payroll")
public class PayrollEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_payroll;

    @Column(name = "total")
    private Double total;

    @Column(name = "payroll_date")
    private LocalDate payrollDate;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @ManyToMany
    @JoinTable(
            name = "payroll_variation",
            joinColumns = @JoinColumn(name = "id_payroll"),
            inverseJoinColumns = @JoinColumn(name = "id_variation")
    )
    private List<PayrollVariationsEntity> payrollVariations;
}
