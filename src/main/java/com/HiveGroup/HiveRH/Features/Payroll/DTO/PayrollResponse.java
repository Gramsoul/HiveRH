package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Features.PayrollDetail.DTO.PayrollDetailResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollResponse {

    private Long idPayroll;

    private String dniEmployee;

    private String employeeName;

    private Long periodId;

    private Integer month;

    private Integer year;

    private Double baseSalarySnapshot;

    private Double totalAdditions;

    private Double totalDeductions;

    private Double total;

    private PayrollStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    private List<PayrollDetailResponseDTO> details;
}
