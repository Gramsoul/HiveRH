package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
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

    // Crear una liquidación de sueldo
    @Transactional
    public PayrollResponse create(PayrollRequest request) {

        validateRequest(request);

        EmployeeEntity employee = findEmployeeById(request.getIdEmployee());

        validateEmployeeCanReceivePayroll(employee, request.getPayrollDate());

        validateEmployeeHasNoPayrollInSameMonth(
                employee,
                request.getPayrollDate(),
                null
        );

        List<VariationEntity> variations = getVariationsByIds(request.getIdVariations());

        Double total = calculatePayrollTotal(employee.getBaseSalary(), variations);

        validatePayrollTotal(total);

        PayrollEntity payroll = PayrollEntity.builder()
                .employee(employee)
                .payrollDate(request.getPayrollDate())
                .total(total)
                .variations(variations)
                .build();

        PayrollEntity savedPayroll = payrollRepository.save(payroll);

        return payrollMapper.toResponse(savedPayroll);
    }

    // Listar todas las liquidaciones
    @Transactional(readOnly = true)
    public List<PayrollResponse> findAll() {

        List<PayrollEntity> payrolls = payrollRepository.findAll();

        return payrollMapper.toResponseList(payrolls);
    }

    // Listar liquidaciones de un empleado con filtros opcionales
    @Transactional(readOnly = true)
    public List<PayrollResponse> findAllByEmployee(Long idEmployee, PayrollFilterDTO filters) {

        EmployeeEntity employee = findEmployeeById(idEmployee);

        PayrollFilterDTO activeFilters = filters != null
                ? filters
                : new PayrollFilterDTO(null, null);

        validateFilterDateRange(activeFilters);

        return payrollRepository.findByEmployee(employee)
                .stream()
                .filter(payroll -> filterByDateRange(payroll, activeFilters))
                .map(payrollMapper::toResponse)
                .toList();
    }

    // Actualizar una liquidación
    @Transactional
    public PayrollResponse updateById(Long id, PayrollRequest request) {

        validateRequest(request);

        PayrollEntity payroll = findPayrollById(id);

        EmployeeEntity employee = findEmployeeById(request.getIdEmployee());

        validateEmployeeCanReceivePayroll(employee, request.getPayrollDate());

        validateEmployeeHasNoPayrollInSameMonth(
                employee,
                request.getPayrollDate(),
                id
        );

        List<VariationEntity> variations = getVariationsByIds(request.getIdVariations());

        Double total = calculatePayrollTotal(employee.getBaseSalary(), variations);

        validatePayrollTotal(total);

        payroll.setEmployee(employee);
        payroll.setPayrollDate(request.getPayrollDate());
        payroll.setTotal(total);
        payroll.setVariations(variations);

        PayrollEntity updatedPayroll = payrollRepository.save(payroll);

        return payrollMapper.toResponse(updatedPayroll);
    }

    // Eliminar una liquidación
    @Transactional
    public PayrollResponse deleteById(Long id) {

        PayrollEntity payroll = findPayrollById(id);

        PayrollResponse response = payrollMapper.toResponse(payroll);

        payrollRepository.delete(payroll);

        return response;
    }

    // Buscar liquidación
    private PayrollEntity findPayrollById(Long id) {

        return payrollRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Liquidación no encontrada",
                        "Payroll"
                ));
    }

    // Validar datos obligatorios
    private void validateRequest(PayrollRequest request) {

        if (request.getIdEmployee() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }

        if (request.getPayrollDate() == null) {
            throw new IllegalArgumentException("La fecha de liquidación es obligatoria");
        }
    }

    // Buscar empleado
    private EmployeeEntity findEmployeeById(Long idEmployee) {

        return employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empleado no encontrado",
                        "Employee"
                ));
    }

    // Validar si el empleado puede recibir liquidación
    private void validateEmployeeCanReceivePayroll(EmployeeEntity employee, LocalDate payrollDate) {

        if (employee.getStatus() != StatusEnum.ACTIVE) {
            throw new IllegalArgumentException("No se puede liquidar sueldo a un empleado que no está activo");
        }

        if (employee.getBaseSalary() == null || employee.getBaseSalary() <= 0) {
            throw new IllegalArgumentException("El empleado no tiene un sueldo base válido");
        }

        if (employee.getHireDate() != null && payrollDate.isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("No se puede liquidar un sueldo antes de la fecha de contratación");
        }
    }

    // Evitar dos liquidaciones en el mismo mes
    private void validateEmployeeHasNoPayrollInSameMonth(
            EmployeeEntity employee,
            LocalDate payrollDate,
            Long idPayrollToIgnore
    ) {

        LocalDate startDate = payrollDate.withDayOfMonth(1);
        LocalDate endDate = payrollDate.withDayOfMonth(payrollDate.lengthOfMonth());

        boolean exists = payrollRepository
                .findByEmployeeAndPayrollDateBetween(employee, startDate, endDate)
                .stream()
                .anyMatch(payroll -> idPayrollToIgnore == null
                        || !payroll.getId_payroll().equals(idPayrollToIgnore));

        if (exists) {
            throw new IllegalArgumentException("El empleado ya tiene una liquidación registrada en ese mes");
        }
    }

    // Buscar las variaciones por sus IDs
    private List<VariationEntity> getVariationsByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Long> uniqueIds = ids.stream()
                .distinct()
                .toList();

        if (uniqueIds.size() != ids.size()) {
            throw new IllegalArgumentException("No se puede repetir una misma variación en la liquidación");
        }

        List<VariationEntity> variations = variationRepository.findAllById(uniqueIds);

        if (variations.size() != uniqueIds.size()) {
            throw new EntityNotFoundException(
                    "Una o más variaciones no existen",
                    "Variation"
            );
        }

        return variations;
    }

    // Calcular el total final de la liquidación
    private Double calculatePayrollTotal(Double baseSalary, List<VariationEntity> variations) {

        Double total = baseSalary;

        for (VariationEntity variation : variations) {
            total += variation.getTotal();
        }

        return total;
    }

    // Validar total final
    private void validatePayrollTotal(Double total) {

        if (total < 0) {
            throw new IllegalArgumentException("El total de la liquidación no puede ser negativo");
        }
    }

    private void validateFilterDateRange(PayrollFilterDTO filters) {

        if (filters.startDate() != null
                && filters.endDate() != null
                && filters.endDate().isBefore(filters.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private boolean filterByDateRange(PayrollEntity payroll, PayrollFilterDTO filters) {

        if (filters.startDate() == null && filters.endDate() == null) {
            return true;
        }

        boolean afterStartDate = filters.startDate() == null || !payroll.getPayrollDate().isBefore(filters.startDate());
        boolean beforeEndDate = filters.endDate() == null || !payroll.getPayrollDate().isAfter(filters.endDate());

        return afterStartDate && beforeEndDate;
    }
}
