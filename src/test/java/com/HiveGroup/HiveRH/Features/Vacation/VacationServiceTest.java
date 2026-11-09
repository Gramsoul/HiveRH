package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Security.Config.SecurityAuthorizationService;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationServiceTest {

    @Mock
    private VacationRepository vacationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private VacationMapper vacationMapper;

    @Mock
    private SecurityAuthorizationService securityAuthorizationService;

    @InjectMocks
    private VacationService vacationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void linkedStaffCanReadOnlyOwnVacationsFilteredByStatusAndDateRange() {
        EmployeeEntity employee = employee(1L, "40111222");
        AccountEntity staff = linkedAccount(10L, "staff.demo", RolEnum.STAFF, employee);
        authenticate(staff);

        VacationEntity matching = vacation(
                1L,
                employee,
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 10, 12),
                LocalDate.of(2026, 10, 16)
        );
        VacationEntity approved = vacation(
                2L,
                employee,
                AbsenceStatus.APPROVED,
                LocalDate.of(2026, 10, 19),
                LocalDate.of(2026, 10, 23)
        );
        VacationEntity outsideRange = vacation(
                3L,
                employee,
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 6)
        );
        VacationResponse expected = responseFor(matching);

        when(vacationRepository.findByEmployee(employee))
                .thenReturn(List.of(matching, approved, outsideRange));
        when(vacationMapper.toResponse(matching)).thenReturn(expected);

        var page = vacationService.findCurrentEmployeeVacations(
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31),
                PageRequest.of(0, 10)
        );

        assertEquals(List.of(expected), page.element());
        assertEquals(1, page.totalElements());
        assertEquals(1, page.totalPages());
        verify(vacationRepository).findByEmployee(employee);
        verify(vacationRepository, never()).findAll();
    }

    @Test
    void findCurrentEmployeeVacationsAppliesPagination() {
        EmployeeEntity employee = employee(1L, "40111222");
        AccountEntity account = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        authenticate(account);

        VacationEntity first = vacation(
                1L,
                employee,
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 10, 12),
                LocalDate.of(2026, 10, 16)
        );
        VacationEntity second = vacation(
                2L,
                employee,
                AbsenceStatus.REJECTED,
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 6)
        );
        VacationResponse secondResponse = responseFor(second);

        when(vacationRepository.findByEmployee(employee)).thenReturn(List.of(first, second));
        when(vacationMapper.toResponse(first)).thenReturn(responseFor(first));
        when(vacationMapper.toResponse(second)).thenReturn(secondResponse);

        var page = vacationService.findCurrentEmployeeVacations(
                null,
                null,
                null,
                PageRequest.of(1, 1)
        );

        assertEquals(List.of(secondResponse), page.element());
        assertEquals(1, page.page());
        assertEquals(1, page.size());
        assertEquals(2, page.totalElements());
        assertEquals(2, page.totalPages());
    }

    @Test
    void findCurrentEmployeeVacationsRejectsAccountWithoutLinkedEmployee() {
        AccountEntity account = AccountEntity.builder()
                .id_account(10L)
                .user("admin")
                .email("admin@hiverh.com")
                .password("encoded")
                .rol(RolEnum.ADMIN)
                .status(AccountStatus.ACTIVE)
                .build();
        authenticate(account);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> vacationService.findCurrentEmployeeVacations(null, null, null, PageRequest.of(0, 10))
        );

        assertEquals("Empleado no encontrado para la cuenta autenticada", exception.getMessage());
        verify(vacationRepository, never()).findAll();
    }

    @Test
    void findCurrentEmployeeVacationsRejectsInvalidDateRange() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vacationService.findCurrentEmployeeVacations(
                        null,
                        LocalDate.of(2026, 10, 31),
                        LocalDate.of(2026, 10, 1),
                        PageRequest.of(0, 10)
                )
        );

        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", exception.getMessage());
        verify(vacationRepository, never()).findAll();
    }

    private void authenticate(AccountEntity account) {
        var authentication = new UsernamePasswordAuthenticationToken(
                account,
                null,
                account.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AccountEntity linkedAccount(Long id, String user, RolEnum role, EmployeeEntity employee) {
        AccountEntity account = AccountEntity.builder()
                .id_account(id)
                .user(user)
                .email(user + "@hiverh.com")
                .password("encoded")
                .rol(role)
                .status(AccountStatus.ACTIVE)
                .employee(employee)
                .build();
        employee.setAccount(account);
        return account;
    }

    private EmployeeEntity employee(Long id, String dni) {
        return EmployeeEntity.builder()
                .id_employee(id)
                .name("Ada")
                .lastName("Lovelace")
                .dni(dni)
                .hireDate(LocalDate.of(2026, 1, 10))
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    private VacationEntity vacation(
            Long id,
            EmployeeEntity employee,
            AbsenceStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return VacationEntity.builder()
                .id_vacation(id)
                .requestDate(LocalDate.of(2026, 9, 1))
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .employee(employee)
                .build();
    }

    private VacationResponse responseFor(VacationEntity vacation) {
        return new VacationResponse(
                vacation.getId_vacation(),
                vacation.getRequestDate(),
                vacation.getStatus(),
                vacation.getStartDate(),
                vacation.getEndDate(),
                null,
                null,
                vacation.getEmployee().getDni(),
                vacation.getEmployee().getName() + " " + vacation.getEmployee().getLastName()
        );
    }
}
