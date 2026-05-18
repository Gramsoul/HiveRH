package com.HiveGroup.HiveRH.Features.Certificate;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateDTO {
    private Long idCertificate;
    private byte[] file;
    private String description;
    private Long idLicense;
}
