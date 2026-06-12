package com.HiveGroup.HiveRH.Features.License.DTO;

import java.time.LocalDate;

public record LicenseFilterDTO(
        Long idEmployee,
        Boolean isAccepted,
        LocalDate startDate,
        LocalDate endDate
) {
}
