package com.HiveGroup.HiveRH.Features.PayrollConcept.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

public record PayrollConceptPatchDTO(
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String name,

        @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
        String description,

        PayrollConceptType type,

        Boolean active
) {
    @AssertTrue(message = "Debe enviar al menos un campo para actualizar")
    public boolean isAnyFieldPresent() {
        return name != null
                || description != null
                || type != null
                || active != null;
    }
}
