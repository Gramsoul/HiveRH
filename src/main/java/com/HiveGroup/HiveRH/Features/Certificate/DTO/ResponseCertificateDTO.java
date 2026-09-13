package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ResponseCertificateDTO(
        String description,
        LocalDate uploadDate,
        Long idLicense
) {
}
