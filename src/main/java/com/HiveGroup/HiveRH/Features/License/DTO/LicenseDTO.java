package com.HiveGroup.HiveRH.Features.License.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseDTO {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private LocalDate requestDate;
    private Boolean isAccepted;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid;
    private String motive;
    private String description;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> idCertificates;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idEmployee;
}
