package com.HiveGroup.HiveRH.Features.Suspension.DTO;

import java.time.LocalDate;

public record SuspensionResponseDTO(
        Long id_suspension,
        Long id_employee,
        String employeeName,
        String employeeLastName,
        String motive,
        LocalDate start_date,
        LocalDate end_date
) {
}
