package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Variation.VariationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "payroll")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PayrollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_payroll;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "payroll_date", nullable = false)
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
    private List<VariationEntity> variations;
}