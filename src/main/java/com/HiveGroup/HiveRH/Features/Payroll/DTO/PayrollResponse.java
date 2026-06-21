package com.HiveGroup.HiveRH.Features.Payroll.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollResponse {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idPayroll;

    private Double total;

    private LocalDate payrollDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long idEmployee;

    private String employeeName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> idVariations;
}
