package com.HiveGroup.HiveRH.Features.Position.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record PositionRequestDTO(
        @JsonAlias("positionName")
        @NotBlank(message = "El nombre del puesto es obligatorio")
        String name
) {
}
