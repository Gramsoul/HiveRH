package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollRequest {

    @NotNull(message = "La fecha de liquidación es obligatoria")
    private LocalDate payrollDate;

    @NotNull(message = "El empleado es obligatorio")
    private Long idEmployee;

    private List<Long> idVariations;
}