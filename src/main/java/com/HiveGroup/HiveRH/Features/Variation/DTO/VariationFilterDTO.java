package com.HiveGroup.HiveRH.Features.Variation.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariationFilterDTO {

    private String title;

    private Double minTotal;

    private Double maxTotal;
}