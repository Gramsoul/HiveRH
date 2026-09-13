package com.HiveGroup.HiveRH.Features.License.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record RequestLicenseDTO(

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate startDate,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate endDate,

        @NotBlank(message = "El motivo de la licencia es obligatorio")
        @Size(max = 100, message = "El motivo no puede superar los 100 caracteres")
        String motive,

        @Size(max = 10, message = "No se pueden asociar más de 10 certificados")
        List<
                @Positive(message = "El ID del certificado debe ser mayor que cero")
                        Long
                > idCertificates
) {
}
