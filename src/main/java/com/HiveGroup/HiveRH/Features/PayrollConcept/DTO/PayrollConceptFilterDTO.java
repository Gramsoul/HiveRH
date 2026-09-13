package com.HiveGroup.HiveRH.Features.PayrollConcept.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;

public record PayrollConceptFilterDTO(
        String name,
        PayrollConceptType type,
        Boolean active
) {
}
