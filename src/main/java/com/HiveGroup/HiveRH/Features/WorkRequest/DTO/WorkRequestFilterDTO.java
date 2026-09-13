package com.HiveGroup.HiveRH.Features.WorkRequest.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;

import java.time.LocalDate;

public record WorkRequestFilterDTO(
        String dniEmployee,
        Long departmentId,
        Long branchId,
        LocalDate from,
        LocalDate to,
        WorkRequestType requestType,
        RequestStatus status
) {
}
