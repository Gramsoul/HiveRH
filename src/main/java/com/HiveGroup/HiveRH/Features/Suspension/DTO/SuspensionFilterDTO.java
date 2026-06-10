package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import java.time.LocalDate;

public record SuspensionFilterDTO(
        Long id_employee,
        LocalDate start_date,
        LocalDate end_date,
        String fullName
) {
}
