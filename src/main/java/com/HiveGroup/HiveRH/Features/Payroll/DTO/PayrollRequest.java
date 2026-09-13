package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import com.HiveGroup.HiveRH.Features.PayrollDetail.DTO.PayrollDetailRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollRequest {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^\\d{7,8}$",
            message = "El DNI debe contener 7 u 8 números, sin puntos, letras ni espacios"
    )
    private String dniEmployee;

    @NotNull(message = "El período de liquidación es obligatorio")
    @Positive(message = "El ID del período debe ser mayor que cero")
    private Long periodId;

    @Valid
    private List<PayrollDetailRequestDTO> details;
}
