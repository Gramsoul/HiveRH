package com.HiveGroup.HiveRH.Features.WorkSchedule.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkScheduleRequestDTO {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(
            regexp = "^\\d{7,8}$",
            message = "El DNI debe contener 7 u 8 numeros, sin puntos, letras ni espacios"
    )
    private String dniEmployee;

    @NotNull(message = "La fecha de trabajo es obligatoria")
    private LocalDate workDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull(message = "El tipo de cronograma es obligatorio")
    private WorkScheduleType type;

    @Size(max = 500, message = "La nota no puede superar los 500 caracteres")
    private String note;
}
