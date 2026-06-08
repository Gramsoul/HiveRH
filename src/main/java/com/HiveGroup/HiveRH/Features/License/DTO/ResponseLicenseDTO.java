package com.HiveGroup.HiveRH.Features.License.DTO;

import lombok.Builder;

import java.time.LocalDate;


@Builder
public record ResponseLicenseDTO(
        Boolean isAccepted,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid,
        String motive,
        String description) {
}
