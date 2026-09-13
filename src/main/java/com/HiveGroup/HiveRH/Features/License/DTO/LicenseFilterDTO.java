package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;

import java.time.LocalDate;

public record LicenseFilterDTO(
        AbsenceStatus status,
        String dniEmployee,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid
) {
}
