package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idCertificate;
    private byte[] file;
    private String description;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idLicense;
}
