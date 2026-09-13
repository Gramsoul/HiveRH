package com.HiveGroup.HiveRH.Features.Certificate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate uploadDate;
    private Long idLicense;
}
