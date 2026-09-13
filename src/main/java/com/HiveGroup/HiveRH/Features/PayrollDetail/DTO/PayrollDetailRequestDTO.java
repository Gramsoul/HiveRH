package com.HiveGroup.HiveRH.Features.PayrollDetail.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PayrollDetailRequestDTO(
        @NotNull(message = "El concepto es obligatorio")
        @Positive(message = "El ID del concepto debe ser mayor que cero")
        Long payrollConceptId,

        @NotNull(message = "El importe es obligatorio")
        @Positive(message = "El importe debe ser mayor que cero")
        Double amount,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description
) {
}
