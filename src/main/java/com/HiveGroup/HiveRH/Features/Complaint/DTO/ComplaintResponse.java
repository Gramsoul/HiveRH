package com.HiveGroup.HiveRH.Features.Complaint.DTO;

import com.HiveGroup.HiveRH.Features.Complaint.ComplaintStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ComplaintResponse(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idComplaint,
        String title,
        String description,
        LocalDate date,
        ComplaintStatusEnum status,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idEmployee,
        String employeeName
) {
}
