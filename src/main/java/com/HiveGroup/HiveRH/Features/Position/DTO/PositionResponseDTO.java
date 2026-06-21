package com.HiveGroup.HiveRH.Features.Position.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PositionResponseDTO(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id_position,
        String name,
        boolean active
) {
}
