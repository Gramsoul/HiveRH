package com.HiveGroup.HiveRH.Features.Variation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "variation")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VariationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_variation;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "total", nullable = false)
    private Double total;
}