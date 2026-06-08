package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import lombok.Builder;

@Builder
public record ResponseCertificateDTO(
        String description,
        Long idLicense
) {
}
