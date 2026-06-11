package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import com.HiveGroup.HiveRH.Features.Variation.VariationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayrollMapper {

    public PayrollResponse toResponse(PayrollEntity payroll) {

        List<Long> idVariations = payroll.getVariations() == null
                ? List.of()
                : payroll.getVariations()
                .stream()
                .map(VariationEntity::getIdVariation)
                .toList();

        return PayrollResponse.builder()
                .idPayroll(payroll.getId_payroll())
                .total(payroll.getTotal())
                .payrollDate(payroll.getPayrollDate())
                .idEmployee(payroll.getEmployee().getId_employee())
                .employeeName(
                        payroll.getEmployee().getName() + " " + payroll.getEmployee().getLastName()
                )
                .idVariations(idVariations)
                .build();
    }

    public List<PayrollResponse> toResponseList(List<PayrollEntity> payrolls) {
        return payrolls.stream()
                .map(this::toResponse)
                .toList();
    }
}