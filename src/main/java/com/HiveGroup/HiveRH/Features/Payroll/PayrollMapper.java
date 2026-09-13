package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import com.HiveGroup.HiveRH.Features.PayrollDetail.DTO.PayrollDetailResponseDTO;
import com.HiveGroup.HiveRH.Features.PayrollDetail.PayrollDetailEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayrollMapper {

    public PayrollResponse toResponse(PayrollEntity payroll) {

        List<PayrollDetailResponseDTO> details = payroll.getDetails() == null
                ? List.of()
                : payroll.getDetails()
                .stream()
                .map(this::toDetailResponse)
                .toList();

        return PayrollResponse.builder()
                .idPayroll(payroll.getId_payroll())
                .dniEmployee(payroll.getEmployee().getDni())
                .employeeName(
                        payroll.getEmployee().getName() + " " + payroll.getEmployee().getLastName()
                )
                .periodId(payroll.getPeriod().getId_payroll_period())
                .month(payroll.getPeriod().getMonth())
                .year(payroll.getPeriod().getYear())
                .baseSalarySnapshot(payroll.getBaseSalarySnapshot())
                .totalAdditions(payroll.getTotalAdditions())
                .totalDeductions(payroll.getTotalDeductions())
                .total(calculateTotal(payroll))
                .status(payroll.getStatus())
                .createdAt(payroll.getCreatedAt())
                .confirmedAt(payroll.getConfirmedAt())
                .details(details)
                .build();
    }

    public List<PayrollResponse> toResponseList(List<PayrollEntity> payrolls) {
        return payrolls.stream()
                .map(this::toResponse)
                .toList();
    }

    private PayrollDetailResponseDTO toDetailResponse(PayrollDetailEntity detail) {
        return new PayrollDetailResponseDTO(
                detail.getId_payroll_detail(),
                detail.getConcept().getId_payroll_concept(),
                detail.getConcept().getName(),
                detail.getConcept().getType(),
                detail.getAmount(),
                detail.getDescription()
        );
    }

    private Double calculateTotal(PayrollEntity payroll) {
        return payroll.getBaseSalarySnapshot()
                + payroll.getTotalAdditions()
                - payroll.getTotalDeductions();
    }
}
