package com.HiveGroup.HiveRH.Features.WorkSchedule.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;

import java.time.LocalDate;

public record WorkScheduleFilterDTO(
        String dniEmployee,
        Long departmentId,
        Long branchId,
        LocalDate from,
        LocalDate to,
        WorkScheduleType type,
        WorkScheduleStatus status
) {
}
