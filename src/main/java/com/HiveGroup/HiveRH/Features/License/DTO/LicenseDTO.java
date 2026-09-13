package com.HiveGroup.HiveRH.Features.License.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
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
    private AbsenceStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPaid;
    private String motive;
    private Long reviewedByAccountId;
    private String reviewComment;
    private List<Long> idCertificates;
    private String dniEmployee;
}
