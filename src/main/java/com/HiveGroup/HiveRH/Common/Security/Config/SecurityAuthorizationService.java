package com.HiveGroup.HiveRH.Common.Security.Config;

import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateRepository;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import com.HiveGroup.HiveRH.Features.Vacation.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityAuthorizationService")
@RequiredArgsConstructor
public class SecurityAuthorizationService {
    private final AccountRepository accountRepository;
    private final LicenseRepository licenseRepository;
    private final CertificateRepository certificateRepository;
    private final VacationRepository vacationRepository;

    public boolean canAccessEmployee(Long employeeId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        AccountEntity account = getCurrentAccount();
        return account != null
                && account.getEmployee() != null
                && account.getEmployee().getId_employee().equals(employeeId);
    }

    public boolean canAccessLicense(Long licenseId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        AccountEntity account = getCurrentAccount();
        return account != null && licenseRepository.findById(licenseId)
                .map(license -> license.getEmployee().getAccount() != null
                        && license.getEmployee().getAccount().getId_account().equals(account.getId_account()))
                .orElse(false);
    }

    public boolean canAccessCertificate(Long certificateId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        AccountEntity account = getCurrentAccount();
        return account != null && certificateRepository.findById(certificateId)
                .map(certificate -> certificate.getLicense().getEmployee().getAccount() != null
                        && certificate.getLicense().getEmployee().getAccount().getId_account().equals(account.getId_account()))
                .orElse(false);
    }

    public boolean canCreateLicenseForEmployee(Long employeeId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        return canAccessEmployee(employeeId);
    }

    public boolean canCreateVacationForEmployee(Long employeeId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        return canAccessEmployee(employeeId);
    }

    public boolean canCreateComplaintForEmployee(Long employeeId) {
        if (hasAnyRole("ROLE_ADMIN", "ROLE_RRHH")) {
            return true;
        }

        return canAccessEmployee(employeeId);
    }

    public boolean canDeleteLicense(Long licenseId) {
        if (hasAnyRole("ROLE_ADMIN")) {
            return true;
        }

        if (hasAnyRole("ROLE_RRHH")) {
            return false;
        }

        AccountEntity account = getCurrentAccount();
        return account != null && licenseRepository.findById(licenseId)
                .map(license -> !license.isAccepted()
                        && license.getEmployee().getAccount() != null
                        && license.getEmployee().getAccount().getId_account().equals(account.getId_account()))
                .orElse(false);
    }

    public boolean canDeleteVacation(Long vacationId) {
        if (hasAnyRole("ROLE_ADMIN")) {
            return true;
        }

        if (hasAnyRole("ROLE_RRHH")) {
            return false;
        }

        AccountEntity account = getCurrentAccount();
        return account != null && vacationRepository.findById(vacationId)
                .map(vacation -> !vacation.isAccepted()
                        && vacation.getEmployee().getAccount() != null
                        && vacation.getEmployee().getAccount().getId_account().equals(account.getId_account()))
                .orElse(false);
    }

    private boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    for (String role : roles) {
                        if (authority.getAuthority().equals(role)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        String username = authentication.getName();
        return accountRepository.findByUserOrEmail(username, username).orElse(null);
    }
}
