package com.HiveGroup.HiveRH.Common.Security.Config;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import com.HiveGroup.HiveRH.Features.Vacation.VacationEntity;
import com.HiveGroup.HiveRH.Features.Vacation.VacationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAuthorizationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private VacationRepository vacationRepository;

    @InjectMocks
    private SecurityAuthorizationService securityAuthorizationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void staffCanDeleteOwnPendingVacation() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        EmployeeEntity employee = employee(1L, "40111222", staff);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));
        when(vacationRepository.findById(1L))
                .thenReturn(Optional.of(vacation(1L, employee, AbsenceStatus.PENDING)));

        assertTrue(securityAuthorizationService.canDeleteVacation(1L));
    }

    @Test
    void staffCannotDeleteAnotherEmployeeVacation() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        AccountEntity otherAccount = account(20L, "employee.demo", RolEnum.EMPLOYEE);
        EmployeeEntity otherEmployee = employee(2L, "40222333", otherAccount);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));
        when(vacationRepository.findById(1L))
                .thenReturn(Optional.of(vacation(1L, otherEmployee, AbsenceStatus.PENDING)));

        assertFalse(securityAuthorizationService.canDeleteVacation(1L));
    }

    @Test
    void staffCannotDeleteOwnApprovedVacation() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        EmployeeEntity employee = employee(1L, "40111222", staff);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));
        when(vacationRepository.findById(1L))
                .thenReturn(Optional.of(vacation(1L, employee, AbsenceStatus.APPROVED)));

        assertFalse(securityAuthorizationService.canDeleteVacation(1L));
    }

    @Test
    void staffCanDeleteOwnPendingLicense() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        EmployeeEntity employee = employee(1L, "40111222", staff);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));
        when(licenseRepository.findById(1L))
                .thenReturn(Optional.of(license(1L, employee, AbsenceStatus.PENDING)));

        assertTrue(securityAuthorizationService.canDeleteLicense(1L));
    }

    @Test
    void adminCanDeleteAnyVacationWithoutOwnershipLookup() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        authenticate(admin);

        assertTrue(securityAuthorizationService.canDeleteVacation(99L));
        verifyNoInteractions(accountRepository, vacationRepository);
    }

    @Test
    void linkedStaffPassesLinkedEmployeeCheck() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        employee(1L, "40111222", staff);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));

        assertTrue(securityAuthorizationService.hasLinkedEmployee());
    }

    @Test
    void accountWithoutEmployeeDoesNotPassLinkedEmployeeCheck() {
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        authenticate(staff);

        when(accountRepository.findByUserOrEmail("staff.demo", "staff.demo"))
                .thenReturn(Optional.of(staff));

        assertFalse(securityAuthorizationService.hasLinkedEmployee());
    }

    private void authenticate(AccountEntity account) {
        var authentication = new UsernamePasswordAuthenticationToken(
                account,
                null,
                account.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AccountEntity account(Long id, String user, RolEnum rol) {
        return AccountEntity.builder()
                .id_account(id)
                .user(user)
                .email(user + "@hiverh.com")
                .password("encoded")
                .rol(rol)
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private EmployeeEntity employee(Long id, String dni, AccountEntity account) {
        EmployeeEntity employee = EmployeeEntity.builder()
                .id_employee(id)
                .name("Ada")
                .lastName("Lovelace")
                .dni(dni)
                .hireDate(LocalDate.of(2026, 1, 10))
                .status(EmployeeStatus.ACTIVE)
                .account(account)
                .build();
        account.setEmployee(employee);
        return employee;
    }

    private VacationEntity vacation(Long id, EmployeeEntity employee, AbsenceStatus status) {
        return VacationEntity.builder()
                .id_vacation(id)
                .employee(employee)
                .requestDate(LocalDate.of(2026, 8, 18))
                .startDate(LocalDate.of(2026, 8, 20))
                .endDate(LocalDate.of(2026, 8, 22))
                .status(status)
                .build();
    }

    private LicenseEntity license(Long id, EmployeeEntity employee, AbsenceStatus status) {
        return LicenseEntity.builder()
                .id_license(id)
                .employee(employee)
                .requestDate(LocalDate.of(2026, 8, 18))
                .startDate(LocalDate.of(2026, 8, 20))
                .endDate(LocalDate.of(2026, 8, 22))
                .status(status)
                .build();
    }
}
