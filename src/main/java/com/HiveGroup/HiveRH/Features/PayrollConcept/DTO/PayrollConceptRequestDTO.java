package com.HiveGroup.HiveRH.Features.PayrollConcept.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PayrollConceptRequestDTO(
        @NotBlank(message = "El nombre del concepto es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description,

        @NotNull(message = "El tipo de concepto es obligatorio")
        PayrollConceptType type,

        Boolean active
) {
}
