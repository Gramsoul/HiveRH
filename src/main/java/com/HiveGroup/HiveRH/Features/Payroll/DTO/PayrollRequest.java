package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

//    @NotNull(message = "El empleado es obligatorio")
//    private Long idEmployee;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^\\d{7,8}$",
            message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
    )
    private String DniEmployee;

    private List<Long> idVariations;
}