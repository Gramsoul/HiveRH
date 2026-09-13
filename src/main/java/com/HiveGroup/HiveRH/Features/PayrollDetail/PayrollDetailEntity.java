package com.HiveGroup.HiveRH.Features.PayrollDetail;

import com.HiveGroup.HiveRH.Features.Payroll.PayrollEntity;
import com.HiveGroup.HiveRH.Features.PayrollConcept.PayrollConceptEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_detail")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PayrollDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_payroll_detail;

    @ManyToOne
    @JoinColumn(name = "id_payroll", nullable = false)
    private PayrollEntity payroll;

    @ManyToOne
    @JoinColumn(name = "id_payroll_concept", nullable = false)
    private PayrollConceptEntity concept;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "description", length = 255)
    private String description;
}
