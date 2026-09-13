package com.HiveGroup.HiveRH.Features.WorkSchedule.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import jakarta.validation.constraints.AssertTrue;
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
public class WorkSchedulePatchDTO {

    @Pattern(
            regexp = "^\\d{7,8}$",
            message = "El DNI debe contener 7 u 8 numeros, sin puntos, letras ni espacios"
    )
    private String dniEmployee;

    private LocalDate workDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private WorkScheduleType type;

    @Size(max = 500, message = "La nota no puede superar los 500 caracteres")
    private String note;

    @AssertTrue(message = "Debe enviar al menos un campo para actualizar")
    public boolean isAnyFieldPresent() {
        return dniEmployee != null
                || workDate != null
                || startTime != null
                || endTime != null
                || type != null
                || note != null;
    }
}
