package com.HiveGroup.HiveRH.Features.Position.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PositionResponseDTO(
        Long id_position,
        String name,
        boolean active
) {
}
