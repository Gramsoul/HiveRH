package com.HiveGroup.HiveRH.Features.Position.DTO;

import jakarta.validation.constraints.NotNull;

public record PositionStatusRequestDTO(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean active
) {
}
