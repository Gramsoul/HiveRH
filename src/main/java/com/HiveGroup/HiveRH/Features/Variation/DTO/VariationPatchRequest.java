package com.HiveGroup.HiveRH.Features.Variation.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariationPatchRequest {

    private String title;

    private String description;

    private Double total;
}