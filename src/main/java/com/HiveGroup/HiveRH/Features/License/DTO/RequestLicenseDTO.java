package com.HiveGroup.HiveRH.Features.License.DTO;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record RequestLicenseDTO(
        LocalDate requestDate,
        Boolean isAccepted,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isPaid,
        String motive,
        String description,
        List<Long> idCertificates,
        Long idEmployee) {
}
