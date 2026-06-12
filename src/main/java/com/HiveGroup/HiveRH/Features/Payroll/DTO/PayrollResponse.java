package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollResponse {

    private Long idPayroll;

    private Double total;

    private LocalDate payrollDate;

    private Long idEmployee;

    private String employeeName;

    private List<Long> idVariations;
}