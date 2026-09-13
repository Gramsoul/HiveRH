package com.HiveGroup.HiveRH.Features.PayrollDetail.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;

public record PayrollDetailResponseDTO(
        Long idPayrollDetail,
        Long payrollConceptId,
        String conceptName,
        PayrollConceptType conceptType,
        Double amount,
        String description
) {
}
