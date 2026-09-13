package com.HiveGroup.HiveRH.Features.PayrollPeriod;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;
import com.HiveGroup.HiveRH.Features.Payroll.PayrollEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "payroll_period",
        uniqueConstraints = @UniqueConstraint(columnNames = {"month", "year"})
)
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PayrollPeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_payroll_period;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PayrollPeriodStatus status = PayrollPeriodStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "period")
    @Builder.Default
    private List<PayrollEntity> payrolls = new ArrayList<>();

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = PayrollPeriodStatus.OPEN;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
