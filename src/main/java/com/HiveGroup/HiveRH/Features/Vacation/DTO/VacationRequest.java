package com.HiveGroup.HiveRH.Features.Vacation.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record VacationRequest(

        @PastOrPresent(message = "La fecha de solicitud no puede ser futura")
        LocalDate requestDate,

        @NotNull(message = "Debe indicar si las vacaciones fueron aceptadas")
        Boolean accepted,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @FutureOrPresent(message = "La fecha de inicio debe ser actual o futura")
        LocalDate startDate,

        @NotNull(message = "La fecha de finalización es obligatoria")
        @FutureOrPresent(message = "La fecha de finalización debe ser actual o futura")
        LocalDate endDate,

        @NotNull(message = "Debe indicar si las vacaciones son pagas")
        Boolean paid,

        @NotNull(message = "El empleado es obligatorio")
        Long idEmployee
) {
}