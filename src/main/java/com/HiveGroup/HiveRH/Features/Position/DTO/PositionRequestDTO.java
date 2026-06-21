package com.HiveGroup.HiveRH.Features.Position.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PositionRequestDTO(

        @JsonAlias("positionName")
        @NotBlank(message = "El nombre del puesto es obligatorio")
        @Size(
                max = 100,
                message = "El nombre del puesto no puede superar los 100 caracteres"
        )
        String name
) {
}