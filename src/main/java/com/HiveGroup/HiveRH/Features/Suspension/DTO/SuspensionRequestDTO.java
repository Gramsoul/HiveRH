package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import java.time.LocalDate;

public record SuspensionRequestDTO(
        Long id_employee,
        String motive,
        LocalDate start_date,
        LocalDate end_date
) {
}
