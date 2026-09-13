package com.HiveGroup.HiveRH.Features.WorkSchedule.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkScheduleResponseDTO(
        Long idWorkSchedule,
        String dniEmployee,
        String employeeName,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        WorkScheduleType type,
        WorkScheduleStatus status,
        String note,
        Long createdByAccountId,
        String createdByUser
) {
}
