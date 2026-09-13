package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.PayrollDetail.PayrollDetailEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "id_payroll_period", nullable = false)
    private PayrollPeriodEntity period;

    @Column(name = "base_salary_snapshot", nullable = false)
    private Double baseSalarySnapshot;

    @Column(name = "total_additions", nullable = false)
    @Builder.Default
    private Double totalAdditions = 0.0;

    @Column(name = "total_deductions", nullable = false)
    @Builder.Default
    private Double totalDeductions = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayrollDetailEntity> details = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = PayrollStatus.DRAFT;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (totalAdditions == null) {
            totalAdditions = 0.0;
        }

        if (totalDeductions == null) {
            totalDeductions = 0.0;
        }
    }
}
