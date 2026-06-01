package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import com.HiveGroup.HiveRH.Features.Variation.VariationEntity;
import com.HiveGroup.HiveRH.Features.Variation.VariationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final VariationRepository variationRepository;
    private final PayrollMapper payrollMapper;

    @Transactional
    public PayrollResponse create(PayrollRequest request) {

        if (request.getIdEmployee() == null) {
            throw new RuntimeException("El empleado es obligatorio");
        }

        if (request.getPayrollDate() == null) {
            throw new RuntimeException("La fecha de liquidación es obligatoria");
        }

        EmployeeEntity employee = employeeRepository.findById(request.getIdEmployee())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (employee.getStatus() != StatusEnum.ACTIVE) {
            throw new RuntimeException("No se puede liquidar sueldo a un empleado que no está activo");
        }

        if (employee.getBaseSalary() == null || employee.getBaseSalary() <= 0) {
            throw new RuntimeException("El empleado no tiene un sueldo base válido");
        }

        if (employee.getHireDate() != null && request.getPayrollDate().isBefore(employee.getHireDate())) {
            throw new RuntimeException("No se puede liquidar un sueldo antes de la fecha de contratación");
        }

        validateEmployeeHasNoPayrollInSameMonth(
                employee.getId_employee(),
                request.getPayrollDate()
        );

        List<VariationEntity> variations = getVariationsByIds(request.getIdVariations());

        Double total = calculatePayrollTotal(employee.getBaseSalary(), variations);

        if (total < 0) {
            throw new RuntimeException("El total de la liquidación no puede ser negativo");
        }

        PayrollEntity payroll = PayrollEntity.builder()
                .employee(employee)
                .payrollDate(request.getPayrollDate())
                .total(total)
                .variations(variations)
                .build();

        PayrollEntity savedPayroll = payrollRepository.save(payroll);

        return payrollMapper.toResponse(savedPayroll);
    }

    @Transactional(readOnly = true)
    public PayrollResponse findById(Long id) {
        PayrollEntity payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));

        return payrollMapper.toResponse(payroll);
    }

    @Transactional(readOnly = true)
    public List<PayrollResponse> findAll() {
        return payrollRepository.findAll()
                .stream()
                .map(payrollMapper::toResponse)
                .toList();
    }

    @Transactional
    public PayrollResponse deleteById(Long id) {
        PayrollEntity payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));

        PayrollResponse response = payrollMapper.toResponse(payroll);

        payrollRepository.delete(payroll);

        return response;
    }

    private List<VariationEntity> getVariationsByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<VariationEntity> variations = variationRepository.findAllById(ids);

        if (variations.size() != ids.size()) {
            throw new RuntimeException("Una o más variaciones no existen");
        }

        return variations;
    }

    private Double calculatePayrollTotal(Double baseSalary, List<VariationEntity> variations) {

        Double total = baseSalary;

        for (VariationEntity variation : variations) {
            total += variation.getTotal();
        }

        return total;
    }

    private void validateEmployeeHasNoPayrollInSameMonth(Long idEmployee, LocalDate payrollDate) {

        LocalDate startDate = payrollDate.withDayOfMonth(1);
        LocalDate endDate = payrollDate.withDayOfMonth(payrollDate.lengthOfMonth());

        boolean exists = payrollRepository.existsPayrollInMonth(
                idEmployee,
                startDate,
                endDate
        );

        if (exists) {
            throw new RuntimeException("El empleado ya tiene una liquidación registrada en ese mes");
        }
    }
}