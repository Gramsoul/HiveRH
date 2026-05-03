package com.HiveGroup.HiveRH.Features.Payroll;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "variation")
public class PayrollVariationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_variation;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "total")
    private double total;
}
