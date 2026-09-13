package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollConceptType;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollPeriodStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollPatchRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import com.HiveGroup.HiveRH.Features.PayrollConcept.PayrollConceptEntity;
import com.HiveGroup.HiveRH.Features.PayrollConcept.PayrollConceptRepository;
import com.HiveGroup.HiveRH.Features.PayrollDetail.DTO.PayrollDetailRequestDTO;
import com.HiveGroup.HiveRH.Features.PayrollDetail.PayrollDetailEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PayrollPeriodRepository payrollPeriodRepository;

    @Mock
    private PayrollConceptRepository payrollConceptRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy
    private PayrollMapper payrollMapper = new PayrollMapper();

    @InjectMocks
    private PayrollService payrollService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createStoresSalarySnapshotAndCalculatesTotalsFromDetails() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollConceptEntity bonus = concept(1L, "Bono", PayrollConceptType.ADDITION, true);
        PayrollConceptEntity advance = concept(2L, "Adelanto", PayrollConceptType.DEDUCTION, true);
        PayrollRequest request = PayrollRequest.builder()
                .dniEmployee("40111222")
                .periodId(1L)
                .details(List.of(
                        new PayrollDetailRequestDTO(1L, 200.0, "  Objetivos  "),
                        new PayrollDetailRequestDTO(2L, 50.0, null)
                ))
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(payrollPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(payrollRepository.findByEmployeeAndPeriod(employee, period)).thenReturn(List.of());
        when(payrollConceptRepository.findById(1L)).thenReturn(Optional.of(bonus));
        when(payrollConceptRepository.findById(2L)).thenReturn(Optional.of(advance));
        when(payrollRepository.save(any(PayrollEntity.class))).thenAnswer(invocation -> {
            PayrollEntity saved = invocation.getArgument(0);
            saved.setId_payroll(7L);
            return saved;
        });

        PayrollResponse response = payrollService.create(request);

        ArgumentCaptor<PayrollEntity> payrollCaptor = ArgumentCaptor.forClass(PayrollEntity.class);
        verify(payrollRepository).save(payrollCaptor.capture());
        PayrollEntity savedPayroll = payrollCaptor.getValue();

        assertEquals(7L, response.getIdPayroll());
        assertEquals(1000.0, savedPayroll.getBaseSalarySnapshot());
        assertEquals(200.0, savedPayroll.getTotalAdditions());
        assertEquals(50.0, savedPayroll.getTotalDeductions());
        assertEquals(1150.0, response.getTotal());
        assertEquals(PayrollStatus.DRAFT, savedPayroll.getStatus());
        assertEquals(2, savedPayroll.getDetails().size());
        assertEquals("Objetivos", savedPayroll.getDetails().get(0).getDescription());
    }

    @Test
    void createRejectsDuplicateConceptInSamePayroll() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollConceptEntity bonus = concept(1L, "Bono", PayrollConceptType.ADDITION, true);
        PayrollRequest request = PayrollRequest.builder()
                .dniEmployee("40111222")
                .periodId(1L)
                .details(List.of(
                        new PayrollDetailRequestDTO(1L, 200.0, "Primer bono"),
                        new PayrollDetailRequestDTO(1L, 100.0, "Bono repetido")
                ))
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(payrollPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(payrollRepository.findByEmployeeAndPeriod(employee, period)).thenReturn(List.of());
        when(payrollConceptRepository.findById(1L)).thenReturn(Optional.of(bonus));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollService.create(request)
        );

        assertEquals("No se puede repetir un concepto en la misma liquidación", exception.getMessage());
        verify(payrollRepository, never()).save(any(PayrollEntity.class));
    }

    @Test
    void createRejectsInactiveConcept() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollConceptEntity inactiveConcept = concept(1L, "Bono viejo", PayrollConceptType.ADDITION, false);
        PayrollRequest request = PayrollRequest.builder()
                .dniEmployee("40111222")
                .periodId(1L)
                .details(List.of(new PayrollDetailRequestDTO(1L, 200.0, null)))
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(payrollPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(payrollRepository.findByEmployeeAndPeriod(employee, period)).thenReturn(List.of());
        when(payrollConceptRepository.findById(1L)).thenReturn(Optional.of(inactiveConcept));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollService.create(request)
        );

        assertEquals("No se puede usar un concepto inactivo en una liquidación", exception.getMessage());
        verify(payrollRepository, never()).save(any(PayrollEntity.class));
    }

    @Test
    void createRejectsNegativePayrollTotal() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollConceptEntity deduction = concept(1L, "Descuento", PayrollConceptType.DEDUCTION, true);
        PayrollRequest request = PayrollRequest.builder()
                .dniEmployee("40111222")
                .periodId(1L)
                .details(List.of(new PayrollDetailRequestDTO(1L, 1200.0, null)))
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(payrollPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(payrollRepository.findByEmployeeAndPeriod(employee, period)).thenReturn(List.of());
        when(payrollConceptRepository.findById(1L)).thenReturn(Optional.of(deduction));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollService.create(request)
        );

        assertEquals("El total de la liquidación no puede ser negativo", exception.getMessage());
        verify(payrollRepository, never()).save(any(PayrollEntity.class));
    }

    @Test
    void confirmSetsConfirmedStatusAndDate() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollConceptEntity bonus = concept(1L, "Bono", PayrollConceptType.ADDITION, true);
        PayrollEntity payroll = payroll(1L, employee, period, PayrollStatus.DRAFT);
        addDetail(payroll, bonus, 100.0);

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        PayrollResponse response = payrollService.confirm(1L);

        assertEquals(PayrollStatus.CONFIRMED, payroll.getStatus());
        assertNotNull(payroll.getConfirmedAt());
        assertEquals(PayrollStatus.CONFIRMED, response.getStatus());
        assertEquals(100.0, response.getTotalAdditions());
        verify(payrollRepository).save(payroll);
    }

    @Test
    void confirmRejectsAlreadyConfirmedPayroll() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollEntity payroll = payroll(1L, employee, period, PayrollStatus.CONFIRMED);

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollService.confirm(1L)
        );

        assertEquals("Solo se pueden modificar liquidaciones en estado DRAFT", exception.getMessage());
        verify(payrollRepository, never()).save(any(PayrollEntity.class));
    }

    @Test
    void confirmRejectsClosedPeriod() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.CLOSED);
        PayrollEntity payroll = payroll(1L, employee, period, PayrollStatus.DRAFT);

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> payrollService.confirm(1L)
        );

        assertEquals("El período de liquidación está cerrado", exception.getMessage());
        verify(payrollRepository, never()).save(any(PayrollEntity.class));
    }

    @Test
    void updateChangingEmployeeRefreshesBaseSalarySnapshot() {
        EmployeeEntity originalEmployee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        EmployeeEntity newEmployee = employee(2L, "40222333", 2500.0, EmployeeStatus.ACTIVE);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollEntity payroll = payroll(1L, originalEmployee, period, PayrollStatus.DRAFT);
        PayrollPatchRequest request = PayrollPatchRequest.builder()
                .dniEmployee("40222333")
                .build();

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
        when(employeeRepository.findByDni("40222333")).thenReturn(Optional.of(newEmployee));
        when(payrollRepository.findByEmployeeAndPeriod(newEmployee, period)).thenReturn(List.of());
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        PayrollResponse response = payrollService.updateById(1L, request);

        assertEquals(newEmployee, payroll.getEmployee());
        assertEquals(2500.0, payroll.getBaseSalarySnapshot());
        assertEquals("40222333", response.getDniEmployee());
        verify(payrollRepository).save(payroll);
    }

    @Test
    void findCurrentEmployeePayrollByIdRejectsDraftPayroll() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollEntity payroll = payroll(1L, employee, period, PayrollStatus.DRAFT);
        authenticate(employeeAccount);

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> payrollService.findCurrentEmployeePayrollById(1L)
        );

        assertEquals("Solo se pueden consultar liquidaciones confirmadas", exception.getMessage());
    }

    @Test
    void findCurrentEmployeePayrollByIdRejectsAnotherEmployeePayroll() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        EmployeeEntity otherEmployee = employee(2L, "40222333", 1000.0, EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        PayrollPeriodEntity period = period(1L, 8, 2026, PayrollPeriodStatus.OPEN);
        PayrollEntity payroll = payroll(1L, otherEmployee, period, PayrollStatus.CONFIRMED);
        authenticate(employeeAccount);

        when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> payrollService.findCurrentEmployeePayrollById(1L)
        );

        assertEquals("No se puede consultar una liquidación de otro empleado", exception.getMessage());
    }

    @Test
    void linkedStaffCanReadOwnConfirmedPayrollsByYear() {
        EmployeeEntity employee = employee(1L, "40111222", 1000.0, EmployeeStatus.ACTIVE);
        AccountEntity staff = linkedAccount(10L, "staff.demo", RolEnum.STAFF, employee);
        PayrollEntity payroll2026 = payroll(
                1L,
                employee,
                period(1L, 8, 2026, PayrollPeriodStatus.OPEN),
                PayrollStatus.CONFIRMED
        );
        PayrollEntity payroll2025 = payroll(
                2L,
                employee,
                period(2L, 12, 2025, PayrollPeriodStatus.OPEN),
                PayrollStatus.CONFIRMED
        );
        authenticate(staff);

        when(payrollRepository.findByEmployeeAndStatus(employee, PayrollStatus.CONFIRMED))
                .thenReturn(List.of(payroll2026, payroll2025));

        List<PayrollResponse> response = payrollService.findCurrentEmployeePayrolls(2026);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getIdPayroll());
        assertEquals(2026, response.get(0).getYear());
    }

    private void authenticate(AccountEntity account) {
        var authentication = new UsernamePasswordAuthenticationToken(
                account,
                null,
                account.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AccountEntity linkedAccount(Long id, String user, RolEnum rol, EmployeeEntity employee) {
        AccountEntity account = AccountEntity.builder()
                .id_account(id)
                .user(user)
                .email(user + "@hiverh.com")
                .password("encoded")
                .rol(rol)
                .status(AccountStatus.ACTIVE)
                .employee(employee)
                .build();
        employee.setAccount(account);
        return account;
    }

    private EmployeeEntity employee(Long id, String dni, Double baseSalary, EmployeeStatus status) {
        return EmployeeEntity.builder()
                .id_employee(id)
                .name("Ada")
                .lastName("Lovelace")
                .dni(dni)
                .hireDate(LocalDate.of(2026, 1, 10))
                .baseSalary(baseSalary)
                .status(status)
                .build();
    }

    private PayrollPeriodEntity period(Long id, Integer month, Integer year, PayrollPeriodStatus status) {
        return PayrollPeriodEntity.builder()
                .id_payroll_period(id)
                .month(month)
                .year(year)
                .status(status)
                .createdAt(LocalDateTime.of(2026, 8, 1, 9, 0))
                .build();
    }

    private PayrollConceptEntity concept(Long id, String name, PayrollConceptType type, boolean active) {
        return PayrollConceptEntity.builder()
                .id_payroll_concept(id)
                .name(name)
                .type(type)
                .active(active)
                .build();
    }

    private PayrollEntity payroll(
            Long id,
            EmployeeEntity employee,
            PayrollPeriodEntity period,
            PayrollStatus status
    ) {
        return PayrollEntity.builder()
                .id_payroll(id)
                .employee(employee)
                .period(period)
                .baseSalarySnapshot(employee.getBaseSalary())
                .totalAdditions(0.0)
                .totalDeductions(0.0)
                .status(status)
                .createdAt(LocalDateTime.of(2026, 8, 18, 10, 0))
                .details(new ArrayList<>())
                .build();
    }

    private void addDetail(PayrollEntity payroll, PayrollConceptEntity concept, Double amount) {
        PayrollDetailEntity detail = PayrollDetailEntity.builder()
                .payroll(payroll)
                .concept(concept)
                .amount(amount)
                .description(concept.getName())
                .build();
        payroll.getDetails().add(detail);
    }
}
