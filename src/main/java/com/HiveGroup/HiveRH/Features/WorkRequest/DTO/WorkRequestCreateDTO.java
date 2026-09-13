package com.HiveGroup.HiveRH.Features.WorkRequest.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkRequestCreateDTO(
        @NotNull(message = "El tipo de solicitud es obligatorio")
        WorkRequestType requestType,

        @NotNull(message = "La fecha objetivo es obligatoria")
        LocalDate targetDate,

        LocalTime startTime,

        LocalTime endTime,

        @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
        String reason,

        @Size(max = 500, message = "La descripcion de compensacion no puede superar los 500 caracteres")
        String compensationDescription
) {
}
