package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Security.Config.SecurityAuthorizationService;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseReviewRequestDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final AccountRepository accountRepository;
    private final CertificateService certificateService;
    private final SecurityAuthorizationService securityAuthorizationService;

    public PageResponseDTO<LicenseDTO> getAllLicensePage(LicenseFilterDTO filters, Pageable pageable) {
        LicenseFilterDTO activeFilters = filters != null
                ? filters
                : new LicenseFilterDTO(null, null, null, null, null);

        validateFilterDateRange(activeFilters);

        List<LicenseDTO> filteredLicenses = licenseRepository.findAll().stream()
                .filter(license -> filterByStatus(license, activeFilters.status()))
                .filter(license -> filterByEmployeeDni(license, activeFilters.dniEmployee()))
                .filter(license -> matchesDateRange(license, activeFilters))
                .filter(license -> filterByPaid(license, activeFilters.isPaid()))
                .map(this::toFullDTO)
                .toList();

        return toPageResponse(filteredLicenses, pageable);
    }

    @Transactional
    public LicenseDTO reviewLicense(Long id, LicenseReviewRequestDTO request) {
        LicenseEntity license = findLicenseById(id);

        validateReviewRequest(request);

        license.setStatus(request.status());
        license.setPaid(request.isPaid());
        license.setReviewComment(request.reviewComment());
        license.setReviewedBy(request.status() == AbsenceStatus.PENDING ? null : getCurrentAccount());

        return toFullDTO(licenseRepository.save(license));
    }

    @Transactional
    public LicenseDTO createLicense(RequestLicenseDTO license){
        validateCreateRequest(license);

        EmployeeEntity employee = getCurrentEmployee();
        LicenseEntity licenseEntity = LicenseEntity.builder()
                .employee(employee)
                .startDate(license.startDate())
                .endDate(license.endDate())
                .motive(license.motive())
                .build();

        if (license.idCertificates() != null) {
            licenseEntity.setCertificates(certificateService.getCertificates(license.idCertificates()));
        }

        return toFullDTO(licenseRepository.save(licenseEntity));
    }

    public void deleteLicense(Long id) {
        LicenseEntity license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Licencia no entrada", "License"));

        if (!securityAuthorizationService.canDeleteLicense(id)) {
            throw new org.springframework.security.access.AccessDeniedException("No tenés permisos para eliminar esta licencia");
        }

        licenseRepository.delete(license);
    }

    public LicenseDTO getLicense(Long id){
        return toFullDTO(findLicenseById(id));
    }

    private LicenseEntity findLicenseById(Long id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Licencia no encontrada", "License"));
    }

    private void validateCreateRequest(RequestLicenseDTO license) {
        if (license.startDate() == null || license.endDate() == null) {
            throw new IllegalArgumentException("Las fechas de licencia son obligatorias");
        }

        if (license.endDate().isBefore(license.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void validateReviewRequest(LicenseReviewRequestDTO request) {
        if (request.status() == null) {
            throw new IllegalArgumentException("El estado de la licencia es obligatorio");
        }

        if (request.isPaid() == null) {
            throw new IllegalArgumentException("Debe indicar si la licencia es paga");
        }
    }

    private boolean matchesDateRange(LicenseEntity license, LicenseFilterDTO filters) {
        if (filters.startDate() == null && filters.endDate() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.endDate() == null || !license.getStartDate().isAfter(filters.endDate());
        boolean endsAfterFilterStart = filters.startDate() == null || !license.getEndDate().isBefore(filters.startDate());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private void validateFilterDateRange(LicenseFilterDTO filters) {
        if (filters.startDate() != null
                && filters.endDate() != null
                && filters.endDate().isBefore(filters.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private boolean filterByStatus(LicenseEntity license, AbsenceStatus status) {
        return status == null || license.getStatus() == status;
    }

    private boolean filterByEmployeeDni(LicenseEntity license, String dniEmployee) {
        if (dniEmployee == null || dniEmployee.isBlank()) {
            return true;
        }

        return license.getEmployee().getDni().equals(dniEmployee);
    }

    private boolean filterByPaid(LicenseEntity license, Boolean isPaid) {
        return isPaid == null || license.isPaid() == isPaid;
    }

    private EmployeeEntity getCurrentEmployee() {
        AccountEntity account = getCurrentAccount();

        if (account.getEmployee() == null) {
            throw new EntityNotFoundException("Empleado no encontrado para la cuenta autenticada", "Employee");
        }

        return account.getEmployee();
    }

    private AccountEntity getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new org.springframework.security.access.AccessDeniedException("No hay usuario autenticado");
        }

        String username = authentication.getName();
        AccountEntity account = accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada", "Account"));

        return account;
    }

    private PageResponseDTO<LicenseDTO> toPageResponse(List<LicenseDTO> licenses, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    licenses,
                    0,
                    licenses.size(),
                    licenses.size(),
                    licenses.isEmpty() ? 0 : 1
            );
        }

        List<LicenseDTO> sortedLicenses = applySort(licenses, pageable.getSort());
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, sortedLicenses.size());
        List<LicenseDTO> content = start >= sortedLicenses.size()
                ? List.of()
                : sortedLicenses.subList(start, end);

        int totalPages = (int) Math.ceil((double) sortedLicenses.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                sortedLicenses.size(),
                totalPages
        );
    }

    private List<LicenseDTO> applySort(List<LicenseDTO> licenses, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return licenses;
        }

        List<LicenseDTO> sortedLicenses = new ArrayList<>(licenses);
        Comparator<LicenseDTO> comparator = null;

        for (Sort.Order order : sort) {
            Comparator<LicenseDTO> nextComparator = getLicenseComparator(order.getProperty());
            if (nextComparator == null) {
                continue;
            }

            if (order.isDescending()) {
                nextComparator = nextComparator.reversed();
            }

            comparator = comparator == null
                    ? nextComparator
                    : comparator.thenComparing(nextComparator);
        }

        if (comparator != null) {
            sortedLicenses.sort(comparator);
        }

        return sortedLicenses;
    }

    private Comparator<LicenseDTO> getLicenseComparator(String property) {
        return switch (property) {
            case "id" -> Comparator.comparing(LicenseDTO::getId, Comparator.nullsLast(Comparator.naturalOrder()));
            case "requestDate" -> Comparator.comparing(LicenseDTO::getRequestDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "status" -> Comparator.comparing(LicenseDTO::getStatus, Comparator.nullsLast(Comparator.naturalOrder()));
            case "startDate" -> Comparator.comparing(LicenseDTO::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "endDate" -> Comparator.comparing(LicenseDTO::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "isPaid" -> Comparator.comparing(LicenseDTO::getIsPaid, Comparator.nullsLast(Comparator.naturalOrder()));
            case "motive" -> Comparator.comparing(LicenseDTO::getMotive, Comparator.nullsLast(String::compareToIgnoreCase));
            case "dniEmployee" -> Comparator.comparing(LicenseDTO::getDniEmployee, Comparator.nullsLast(String::compareToIgnoreCase));
            default -> null;
        };
    }

    private LicenseDTO toFullDTO(LicenseEntity license) {
        return LicenseDTO.builder()
                .id(license.getId_license())
                .requestDate(license.getRequestDate())
                .status(license.getStatus())
                .startDate(license.getStartDate())
                .endDate(license.getEndDate())
                .isPaid(license.isPaid())
                .motive(license.getMotive())
                .reviewedByAccountId(license.getReviewedBy() != null ? license.getReviewedBy().getId_account() : null)
                .reviewComment(license.getReviewComment())
                .idCertificates(certificateService.getCertificateID(license.getCertificates()))
                .dniEmployee(license.getEmployee().getDni())
                .build();
    }

}
