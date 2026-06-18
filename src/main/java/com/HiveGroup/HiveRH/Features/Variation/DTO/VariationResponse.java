package com.HiveGroup.HiveRH.Features.Variation.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariationResponse {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idVariation;

    private String title;

    private String description;

    private Double total;
}
