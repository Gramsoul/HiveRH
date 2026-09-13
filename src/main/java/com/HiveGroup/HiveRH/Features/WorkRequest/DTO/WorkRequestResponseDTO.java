package com.HiveGroup.HiveRH.Features.WorkRequest.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkRequestResponseDTO(
        Long idWorkRequest,
        String dniEmployee,
        String employeeName,
        WorkRequestType requestType,
        LocalDate requestDate,
        LocalDate targetDate,
        LocalTime startTime,
        LocalTime endTime,
        String reason,
        String compensationDescription,
        RequestStatus status,
        Long reviewedByAccountId,
        String reviewedByUser,
        String reviewComment
) {
}
