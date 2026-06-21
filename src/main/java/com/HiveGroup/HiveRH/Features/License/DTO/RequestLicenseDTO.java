package com.HiveGroup.HiveRH.Features.License.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record RequestLicenseDTO(
        @NotNull(message = "La fecha de solicitud es obligatoria")
        LocalDate requestDate,
        @NotNull(message = "Debe indicar si la licencia fue aceptada")
        Boolean isAccepted,
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,
        @NotNull(message = "La fecha de finalizacion es obligatoria")
        LocalDate endDate,
        @NotNull(message = "Debe indicar si la licencia es paga")
        Boolean isPaid,
        @NotBlank(message = "El motivo es obligatorio")
        String motive,
        String description,
        List<Long> idCertificates,
        @NotNull(message = "El empleado es obligatorio")
        Long idEmployee) {
}
