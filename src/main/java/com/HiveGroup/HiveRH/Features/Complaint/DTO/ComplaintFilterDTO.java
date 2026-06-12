package com.HiveGroup.HiveRH.Features.Complaint.DTO;

import com.HiveGroup.HiveRH.Features.Complaint.ComplaintStatusEnum;

import java.time.LocalDate;

public record ComplaintFilterDTO(
        Long idComplaint,
        String title,
        ComplaintStatusEnum status,
        LocalDate startDate,
        LocalDate endDate
) {
}
