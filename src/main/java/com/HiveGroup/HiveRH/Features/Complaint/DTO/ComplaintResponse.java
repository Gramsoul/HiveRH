package com.HiveGroup.HiveRH.Features.Complaint.DTO;

import com.HiveGroup.HiveRH.Features.Complaint.ComplaintStatusEnum;

import java.time.LocalDate;

public record ComplaintResponse(
        Long idComplaint,
        String title,
        String description,
        LocalDate date,
        ComplaintStatusEnum status,
        Long idEmployee,
        String employeeName
) {
}