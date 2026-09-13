package com.HiveGroup.HiveRH.Features.PayrollConcept.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;

public record PayrollConceptResponseDTO(
        Long idPayrollConcept,
        String name,
        String description,
        PayrollConceptType type,
        boolean active
) {
}
