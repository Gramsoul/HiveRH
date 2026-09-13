package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollPatchRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import com.HiveGroup.HiveRH.Features.PayrollConcept.PayrollConceptEntity;
import com.HiveGroup.HiveRH.Features.PayrollConcept.PayrollConceptRepository;
import com.HiveGroup.HiveRH.Features.PayrollDetail.DTO.PayrollDetailRequestDTO;
import com.HiveGroup.HiveRH.Features.PayrollDetail.PayrollDetailEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;
    private final PayrollConceptRepository payrollConceptRepository;
    private final AccountRepository accountRepository;
    private final PayrollMapper payrollMapper;

    @Transactional
    public PayrollResponse create(PayrollRequest request) {
        validateCreateRequest(request);

        EmployeeEntity employee = findEmployeeByDni(request.getDniEmployee());
        PayrollPeriodEntity period = findPeriodById(request.getPeriodId());

        validatePeriodOpen(period);
        validateEmployeeCanReceivePayroll(employee, period);
        validateEmployeeHasNoActivePayrollInPeriod(employee, period, null);

        PayrollEntity payroll = PayrollEntity.builder()
                .employee(employee)
                .period(period)
                .baseSalarySnapshot(employee.getBaseSalary())
                .totalAdditions(0.0)
                .totalDeductions(0.0)
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .details(new ArrayList<>())
                .build();

        replaceDetails(payroll, request.getDetails());
        recalculateTotals(payroll);
        validatePayrollTotal(payroll);

        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<PayrollResponse> findAllByFilter(PayrollFilterDTO filters, Pageable pageable) {
        PayrollFilterDTO activeFilters = filters != null
                ? filters
                : new PayrollFilterDTO(null, null, null, null, null);

        validatePayrollFilters(activeFilters);

        List<PayrollResponse> payrolls = payrollRepository.findAll()
                .stream()
                .filter(payroll -> activeFilters.periodId() == null
                        || Objects.equals(payroll.getPeriod().getId_payroll_period(), activeFilters.periodId()))
                .filter(payroll -> activeFilters.month() == null
                        || Objects.equals(payroll.getPeriod().getMonth(), activeFilters.month()))
                .filter(payroll -> activeFilters.year() == null
                        || Objects.equals(payroll.getPeriod().getYear(), activeFilters.year()))
                .filter(payroll -> activeFilters.status() == null || payroll.getStatus() == activeFilters.status())
                .filter(payroll -> activeFilters.dniEmployee() == null
                        || payroll.getEmployee().getDni().equals(activeFilters.dniEmployee()))
                .map(payrollMapper::toResponse)
                .toList();

        return toPageResponse(payrolls, pageable);
    }

    @Transactional(readOnly = true)
    public PayrollResponse findById(Long id) {
        return payrollMapper.toResponse(findPayrollById(id));
    }

    @Transactional(readOnly = true)
    public List<PayrollResponse> findCurrentEmployeePayrolls(Integer year) {
        validateYearFilter(year);

        EmployeeEntity employee = findCurrentEmployee();

        return payrollRepository.findByEmployeeAndStatus(employee, PayrollStatus.CONFIRMED)
                .stream()
                .filter(payroll -> year == null || Objects.equals(payroll.getPeriod().getYear(), year))
                .map(payrollMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PayrollResponse findCurrentEmployeePayrollById(Long id) {
        EmployeeEntity employee = findCurrentEmployee();
        PayrollEntity payroll = findPayrollById(id);

        if (payroll.getStatus() != PayrollStatus.CONFIRMED) {
            throw new AccessDeniedException("Solo se pueden consultar liquidaciones confirmadas");
        }

        if (!payroll.getEmployee().getId_employee().equals(employee.getId_employee())) {
            throw new AccessDeniedException("No se puede consultar una liquidación de otro empleado");
        }

        return payrollMapper.toResponse(payroll);
    }

    @Transactional
    public PayrollResponse updateById(Long id, PayrollPatchRequest request) {
        validatePatchRequest(request);

        PayrollEntity payroll = findPayrollById(id);
        validatePayrollEditable(payroll);

        EmployeeEntity employee = payroll.getEmployee();
        PayrollPeriodEntity period = payroll.getPeriod();

        if (request.getDniEmployee() != null) {
            employee = findEmployeeByDni(request.getDniEmployee());
        }

        if (request.getPeriodId() != null) {
            period = findPeriodById(request.getPeriodId());
        }

        validatePeriodOpen(period);
        validateEmployeeCanReceivePayroll(employee, period);
        validateEmployeeHasNoActivePayrollInPeriod(employee, period, id);

        if (!payroll.getEmployee().getId_employee().equals(employee.getId_employee())) {
            payroll.setBaseSalarySnapshot(employee.getBaseSalary());
        }

        payroll.setEmployee(employee);
        payroll.setPeriod(period);

        if (request.getDetails() != null) {
            replaceDetails(payroll, request.getDetails());
        }

        recalculateTotals(payroll);
        validatePayrollTotal(payroll);

        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Transactional
    public PayrollResponse confirm(Long id) {
        PayrollEntity payroll = findPayrollById(id);
        validatePayrollEditable(payroll);
        validatePeriodOpen(payroll.getPeriod());

        recalculateTotals(payroll);
        validatePayrollTotal(payroll);

        payroll.setStatus(PayrollStatus.CONFIRMED);
        payroll.setConfirmedAt(LocalDateTime.now());

        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Transactional
    public PayrollResponse cancel(Long id) {
        PayrollEntity payroll = findPayrollById(id);

        if (payroll.getStatus() == PayrollStatus.CANCELLED) {
            throw new IllegalArgumentException("La liquidación ya está anulada");
        }

        validatePeriodOpen(payroll.getPeriod());

        payroll.setStatus(PayrollStatus.CANCELLED);

        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    private void replaceDetails(PayrollEntity payroll, List<PayrollDetailRequestDTO> detailRequests) {
        List<PayrollDetailRequestDTO> activeRequests = detailRequests == null
                ? List.of()
                : detailRequests;

        Set<Long> conceptIds = new HashSet<>();

        if (payroll.getDetails() == null) {
            payroll.setDetails(new ArrayList<>());
        }

        payroll.getDetails().clear();

        for (PayrollDetailRequestDTO detailRequest : activeRequests) {
            validateDetailRequest(detailRequest);

            if (!conceptIds.add(detailRequest.payrollConceptId())) {
                throw new IllegalArgumentException("No se puede repetir un concepto en la misma liquidación");
            }

            PayrollConceptEntity concept = findConceptById(detailRequest.payrollConceptId());

            if (!concept.isActive()) {
                throw new IllegalArgumentException("No se puede usar un concepto inactivo en una liquidación");
            }

            PayrollDetailEntity detail = PayrollDetailEntity.builder()
                    .payroll(payroll)
                    .concept(concept)
                    .amount(detailRequest.amount())
                    .description(normalizeDescription(detailRequest.description()))
                    .build();

            payroll.getDetails().add(detail);
        }
    }

    private void recalculateTotals(PayrollEntity payroll) {
        double additions = 0.0;
        double deductions = 0.0;

        if (payroll.getDetails() != null) {
            for (PayrollDetailEntity detail : payroll.getDetails()) {
                if (detail.getConcept().getType() == PayrollConceptType.ADDITION) {
                    additions += detail.getAmount();
                } else {
                    deductions += detail.getAmount();
                }
            }
        }

        payroll.setTotalAdditions(additions);
        payroll.setTotalDeductions(deductions);
    }

    private void validateCreateRequest(PayrollRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de liquidación es obligatoria");
        }

        if (request.getDniEmployee() == null || request.getDniEmployee().isBlank()) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }

        if (request.getPeriodId() == null) {
            throw new IllegalArgumentException("El período de liquidación es obligatorio");
        }
    }

    private void validatePatchRequest(PayrollPatchRequest request) {
        if (request == null || !request.isAnyFieldPresent()) {
            throw new IllegalArgumentException("Debe enviar al menos un campo para actualizar");
        }

        if (request.getDniEmployee() != null && request.getDniEmployee().isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
    }

    private void validateDetailRequest(PayrollDetailRequestDTO detailRequest) {
        if (detailRequest == null) {
            throw new IllegalArgumentException("El detalle de liquidación es obligatorio");
        }

        if (detailRequest.payrollConceptId() == null) {
            throw new IllegalArgumentException("El concepto es obligatorio");
        }

        if (detailRequest.amount() == null || detailRequest.amount() <= 0) {
            throw new IllegalArgumentException("El importe del detalle debe ser mayor que cero");
        }
    }

    private void validateEmployeeCanReceivePayroll(EmployeeEntity employee, PayrollPeriodEntity period) {
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("No se puede liquidar sueldo a un empleado que no está activo");
        }

        if (employee.getBaseSalary() == null || employee.getBaseSalary() <= 0) {
            throw new IllegalArgumentException("El empleado no tiene un sueldo base válido");
        }

        YearMonth yearMonth = YearMonth.of(period.getYear(), period.getMonth());
        LocalDate periodEnd = yearMonth.atEndOfMonth();

        if (employee.getHireDate() != null && employee.getHireDate().isAfter(periodEnd)) {
            throw new IllegalArgumentException("No se puede liquidar un período anterior a la contratación del empleado");
        }
    }

    private void validateEmployeeHasNoActivePayrollInPeriod(
            EmployeeEntity employee,
            PayrollPeriodEntity period,
            Long idPayrollToIgnore
    ) {
        payrollRepository.findByEmployeeAndPeriod(employee, period)
                .stream()
                .filter(payroll -> payroll.getStatus() != PayrollStatus.CANCELLED)
                .filter(payroll -> idPayrollToIgnore == null || !payroll.getId_payroll().equals(idPayrollToIgnore))
                .findFirst()
                .ifPresent(payroll -> {
                    throw new IllegalArgumentException("El empleado ya tiene una liquidación activa para ese período");
                });
    }

    private void validatePayrollEditable(PayrollEntity payroll) {
        if (payroll.getStatus() != PayrollStatus.DRAFT) {
            throw new IllegalArgumentException("Solo se pueden modificar liquidaciones en estado DRAFT");
        }
    }

    private void validatePeriodOpen(PayrollPeriodEntity period) {
        if (period.getStatus() != PayrollPeriodStatus.OPEN) {
            throw new IllegalArgumentException("El período de liquidación está cerrado");
        }
    }

    private void validatePayrollTotal(PayrollEntity payroll) {
        double total = payroll.getBaseSalarySnapshot()
                + payroll.getTotalAdditions()
                - payroll.getTotalDeductions();

        if (total < 0) {
            throw new IllegalArgumentException("El total de la liquidación no puede ser negativo");
        }
    }

    private void validateYearFilter(Integer year) {
        if (year != null && year < 2000) {
            throw new IllegalArgumentException("El año debe ser mayor o igual a 2000");
        }
    }

    private void validatePayrollFilters(PayrollFilterDTO filters) {
        if (filters.periodId() != null && filters.periodId() <= 0) {
            throw new IllegalArgumentException("El ID del período debe ser mayor que cero");
        }

        if (filters.month() != null && (filters.month() < 1 || filters.month() > 12)) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        validateYearFilter(filters.year());

        if (filters.dniEmployee() != null && filters.dniEmployee().isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
    }

    private EmployeeEntity findEmployeeByDni(String dniEmployee) {
        return employeeRepository.findByDni(dniEmployee)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado", "Employee"));
    }

    private PayrollPeriodEntity findPeriodById(Long periodId) {
        return payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new EntityNotFoundException("Período de liquidación no encontrado", "PayrollPeriod"));
    }

    private PayrollConceptEntity findConceptById(Long conceptId) {
        return payrollConceptRepository.findById(conceptId)
                .orElseThrow(() -> new EntityNotFoundException("Concepto de liquidación no encontrado", "PayrollConcept"));
    }

    private PayrollEntity findPayrollById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("La liquidación es obligatoria");
        }

        return payrollRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Liquidación no encontrada", "Payroll"));
    }

    private EmployeeEntity findCurrentEmployee() {
        AccountEntity account = getCurrentAccount();

        if (account.getEmployee() == null) {
            throw new EntityNotFoundException("Empleado no encontrado para la cuenta autenticada", "Employee");
        }

        return account.getEmployee();
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AccountEntity account) {
            return account;
        }

        String username = authentication.getName();

        return accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta inexistente", "AccountEntity"));
    }

    private String normalizeDescription(String description) {
        return description == null || description.isBlank()
                ? null
                : description.trim();
    }

    private PageResponseDTO<PayrollResponse> toPageResponse(List<PayrollResponse> payrolls, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    payrolls,
                    0,
                    payrolls.size(),
                    payrolls.size(),
                    payrolls.isEmpty() ? 0 : 1
            );
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, payrolls.size());
        List<PayrollResponse> content = start >= payrolls.size()
                ? List.of()
                : payrolls.subList(start, end);

        int totalPages = (int) Math.ceil((double) payrolls.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                payrolls.size(),
                totalPages
        );
    }
}
