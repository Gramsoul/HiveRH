package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Security.Config.SecurityAuthorizationService;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Mock
    private LicenseRepository licenseRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CertificateService certificateService;

    @Mock
    private SecurityAuthorizationService securityAuthorizationService;

    @InjectMocks
    private LicenseService licenseService;

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentEmployeeLicensesOnlyReturnsOwnMatchingLicenses() {
        EmployeeEntity employee = EmployeeEntity.builder()
                .id_employee(1L)
                .dni("40111222")
                .build();
        AccountEntity account = AccountEntity.builder()
                .id_account(10L)
                .user("40111222")
                .employee(employee)
                .build();
        LicenseEntity matching = license(
                1L,
                employee,
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 11, 4),
                LocalDate.of(2026, 11, 6),
                false
        );
        LicenseEntity approved = license(
                2L,
                employee,
                AbsenceStatus.APPROVED,
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 12),
                true
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("40111222", null, List.of())
        );
        when(accountRepository.findByUserOrEmail("40111222", "40111222"))
                .thenReturn(Optional.of(account));
        when(licenseRepository.findByEmployee(employee)).thenReturn(List.of(matching, approved));
        when(certificateService.getCertificateID(List.of())).thenReturn(List.of());

        var page = licenseService.getCurrentEmployeeLicenses(
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30),
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.totalElements());
        assertEquals(1, page.totalPages());
        assertEquals(List.of(1L), page.element().stream().map(dto -> dto.getId()).toList());
        assertEquals("40111222", page.element().get(0).getDniEmployee());
        verify(licenseRepository).findByEmployee(employee);
        verify(licenseRepository, never()).findAll();
    }

    private LicenseEntity license(
            Long id,
            EmployeeEntity employee,
            AbsenceStatus status,
            LocalDate startDate,
            LocalDate endDate,
            boolean isPaid
    ) {
        return LicenseEntity.builder()
                .id_license(id)
                .employee(employee)
                .requestDate(LocalDate.of(2026, 11, 4))
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .isPaid(isPaid)
                .motive("Reposo")
                .certificates(List.of())
                .build();
    }
}
