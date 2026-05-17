package com.HiveGroup.HiveRH.Features.License;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDTO {
    private Long id;
    private LocalDate requestDate;
    private Boolean isAccepted;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid;
    private String motive;
    private String description;
    private List<Long> idCertificates;
    private Long idEmployee;
}
