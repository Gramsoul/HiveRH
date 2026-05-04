package com.HiveGroup.HiveRH.Features.Varation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variation")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
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
