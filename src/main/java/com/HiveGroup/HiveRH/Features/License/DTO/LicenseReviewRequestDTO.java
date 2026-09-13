package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LicenseReviewRequestDTO(
        @NotNull(message = "El estado de la licencia es obligatorio")
        AbsenceStatus status,

        @NotNull(message = "Debe indicar si la licencia es paga")
        Boolean isPaid,

        @Size(max = 500, message = "El comentario de revisión no puede superar los 500 caracteres")
        String reviewComment
) {
}
