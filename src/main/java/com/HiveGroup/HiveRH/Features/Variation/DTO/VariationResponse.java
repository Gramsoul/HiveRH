package com.HiveGroup.HiveRH.Features.Variation.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariationResponse {

    private Long idVariation;

    private String title;

    private String description;

    private Double total;
}