package com.HiveGroup.HiveRH.Features.PayrollConcept;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;
import com.HiveGroup.HiveRH.Features.PayrollDetail.PayrollDetailEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payroll_concept")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PayrollConceptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_payroll_concept;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PayrollConceptType type;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "concept")
    @Builder.Default
    private List<PayrollDetailEntity> details = new ArrayList<>();
}
