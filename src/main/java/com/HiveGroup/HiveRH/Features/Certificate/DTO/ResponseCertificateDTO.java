package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ResponseCertificateDTO(
        String description,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long idLicense
) {
}
