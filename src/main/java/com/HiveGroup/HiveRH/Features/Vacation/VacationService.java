package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Security.Config.SecurityAuthorizationService;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationFilterDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final VacationMapper vacationMapper;
    private final SecurityAuthorizationService securityAuthorizationService;

    // Crear vacaciones
    @Transactional
    public VacationResponse create(VacationRequest request) {

        // Valida que los datos obligatorios del request.
        validateRequest(request);

        EmployeeEntity employee = findEmployeeById(request.dniEmployee());

        validateEmployeeCanHaveVacation(employee, request.startDate());

        // Valida que las fechas sean correctas.
        validateVacationDates(
                request.requestDate(),
                request.startDate(),
                request.endDate()
        );

        /*Valida que este empleado no tenga otra vacación registrada que se cruce con el mismo período.
        El último parámetro es null porque estamos creando una nueva vacación, no actualizando una existente.*/
        validateEmployeeHasNoVacationInSamePeriod(
                employee,
                request.startDate(),
                request.endDate(),
                null
        );

        VacationEntity vacation = vacationMapper.toEntity(request, employee);
        vacation.setStatus(AbsenceStatus.PENDING);
        vacation.setReviewedBy(null);
        vacation.setReviewComment(null);

        VacationEntity savedVacation = vacationRepository.save(vacation);

        return vacationMapper.toResponse(savedVacation);
    }

    // Buscar vacaciones por ID
    @Transactional(readOnly = true)
    public VacationResponse findById(Long idVacation) {

        VacationEntity vacation = findVacationById(idVacation);

        return vacationMapper.toResponse(vacation);
    }

    // Listar vacaciones con filtros
    @Transactional(readOnly = true)
    public PageResponseDTO<VacationResponse> findAllByFilter(VacationFilterDTO filters, Pageable pageable) {

        VacationFilterDTO activeFilters = filters != null
                ? filters
                : new VacationFilterDTO(null, null, null, null, null);

        validateFilterDateRange(activeFilters);

        List<VacationResponse> filteredVacations = vacationRepository.findAll()
                .stream()
                .filter(vacation -> filterByStatus(vacation, activeFilters.status()))
                .filter(vacation -> filterByDateRange(vacation, activeFilters))
                .filter(vacation -> filterByEmployeeDni(vacation, activeFilters.dniEmployee()))
                .filter(vacation -> filterByEmployeeFullName(vacation, activeFilters.fullName()))
                .map(vacationMapper::toResponse)
                .toList();

        return toPageResponse(filteredVacations, pageable);
    }

    // Actualizar vacaciones
    @Transactional
    public VacationResponse updateById(Long idVacation, VacationRequest request) {

        validateRequest(request);

        VacationEntity vacation = findVacationById(idVacation);

        EmployeeEntity employee = findEmployeeById(request.dniEmployee());

        validateEmployeeCanHaveVacation(employee, request.startDate());

        validateVacationDates(
                request.requestDate(),
                request.startDate(),
                request.endDate()
        );

        validateEmployeeHasNoVacationInSamePeriod(
                employee,
                request.startDate(),
                request.endDate(),
                idVacation
        );

        vacation.setRequestDate(
                request.requestDate() != null
                        ? request.requestDate()
                        : LocalDate.now()
        );

        applyReviewData(vacation, request);
        vacation.setStartDate(request.startDate());
        vacation.setEndDate(request.endDate());
        vacation.setEmployee(employee);

        VacationEntity updatedVacation = vacationRepository.save(vacation);

        return vacationMapper.toResponse(updatedVacation);
    }

    // Eliminar vacaciones
    @Transactional
    public VacationResponse deleteById(Long idVacation) {

        VacationEntity vacation = findVacationById(idVacation);

        if (!securityAuthorizationService.canDeleteVacation(idVacation)) {
            throw new org.springframework.security.access.AccessDeniedException("No tenés permisos para eliminar estas vacaciones");
        }

        VacationResponse response = vacationMapper.toResponse(vacation);

        vacationRepository.delete(vacation);

        return response;
    }

    // Buscar vacaciones o lanzar error
    private VacationEntity findVacationById(Long idVacation) {

        return vacationRepository.findById(idVacation)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vacación no encontrada",
                        "Vacation"
                ));
    }

    // Buscar empleado o lanzar error
    private EmployeeEntity findEmployeeById(String dniEmployee) {

        return employeeRepository.findByDni(dniEmployee)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empleado no encontrado",
                        "Employee"
                ));
    }

    // Validar datos obligatorios
    private void validateRequest(VacationRequest request) {

        if (request.dniEmployee() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }

        if (request.startDate() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }

        if (request.endDate() == null) {
            throw new IllegalArgumentException("La fecha de finalización es obligatoria");
        }
    }

    // Validar si el empleado puede tener vacaciones
    private void validateEmployeeCanHaveVacation(EmployeeEntity employee, LocalDate startDate) {

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("No se pueden registrar vacaciones para un empleado que no está activo");
        }

        if (employee.getHireDate() != null && startDate.isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("No se pueden registrar vacaciones antes de la fecha de contratación");
        }
    }

    // Validar fechas de vacaciones
    private void validateVacationDates(LocalDate requestDate, LocalDate startDate, LocalDate endDate) {

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha de inicio");
        }

        if (requestDate != null && requestDate.isAfter(startDate)) {
            throw new IllegalArgumentException("La fecha de solicitud no puede ser posterior al inicio de las vacaciones");
        }

        LocalDate effectiveRequestDate = requestDate != null ? requestDate : LocalDate.now();
        if (countBusinessDaysBetween(effectiveRequestDate, startDate) < 5) {
            throw new IllegalArgumentException("Las vacaciones deben solicitarse con al menos 5 días hábiles de anticipación");
        }
    }

    // Evitar vacaciones superpuestas
    private void validateEmployeeHasNoVacationInSamePeriod(
            EmployeeEntity employee,
            LocalDate startDate,
            LocalDate endDate,
            Long idVacationToIgnore
    ) {

        boolean exists = vacationRepository.findByEmployee(employee)
                .stream()
                .filter(vacation -> idVacationToIgnore == null
                        || !vacation.getId_vacation().equals(idVacationToIgnore))
                .anyMatch(vacation -> datesOverlap(
                        vacation.getStartDate(),
                        vacation.getEndDate(),
                        startDate,
                        endDate
                ));

        if (exists) {
            throw new IllegalArgumentException("El empleado ya tiene vacaciones registradas en ese período");
        }
    }

    // Validar si dos rangos de fechas se pisan
    private boolean datesOverlap(
            LocalDate existingStartDate,
            LocalDate existingEndDate,
            LocalDate newStartDate,
            LocalDate newEndDate
    ) {

        return !existingStartDate.isAfter(newEndDate)
                && !existingEndDate.isBefore(newStartDate);
    }

    private void validateFilterDateRange(VacationFilterDTO filters) {

        if (filters.startDate() != null
                && filters.endDate() != null
                && filters.endDate().isBefore(filters.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private boolean filterByStatus(VacationEntity vacation, AbsenceStatus status) {

        return status == null || vacation.getStatus() == status;
    }

    private boolean filterByDateRange(VacationEntity vacation, VacationFilterDTO filters) {

        if (filters.startDate() == null && filters.endDate() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.endDate() == null || !vacation.getStartDate().isAfter(filters.endDate());
        boolean endsAfterFilterStart = filters.startDate() == null || !vacation.getEndDate().isBefore(filters.startDate());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private boolean filterByEmployeeDni(VacationEntity vacation, String dniEmployee) {

        return dniEmployee == null || dniEmployee.isBlank() || vacation.getEmployee().getDni().equals(dniEmployee);
    }

    private boolean filterByEmployeeFullName(VacationEntity vacation, String fullName) {

        return TextSearchUtils.matchesFullName(
                vacation.getEmployee().getName(),
                vacation.getEmployee().getLastName(),
                fullName
        );
    }

    private void applyReviewData(VacationEntity vacation, VacationRequest request) {
        if (request.status() != null) {
            vacation.setStatus(request.status());
            vacation.setReviewedBy(request.status() == AbsenceStatus.PENDING ? null : getCurrentAccount());
        }

        vacation.setReviewComment(request.reviewComment());
    }

    private AccountEntity getCurrentAccount() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new org.springframework.security.access.AccessDeniedException("No hay usuario autenticado");
        }

        String username = authentication.getName();
        return accountRepository.findByUserOrEmail(username, username)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada", "Account"));
    }

    private int countBusinessDaysBetween(LocalDate fromExclusive, LocalDate toInclusive) {
        int businessDays = 0;
        LocalDate date = fromExclusive.plusDays(1);

        while (!date.isAfter(toInclusive)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY
                    && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                businessDays++;
            }

            date = date.plusDays(1);
        }

        return businessDays;
    }

    private PageResponseDTO<VacationResponse> toPageResponse(List<VacationResponse> vacations, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    vacations,
                    0,
                    vacations.size(),
                    vacations.size(),
                    vacations.isEmpty() ? 0 : 1
            );
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, vacations.size());
        List<VacationResponse> content = start >= vacations.size()
                ? List.of()
                : vacations.subList(start, end);

        int totalPages = (int) Math.ceil((double) vacations.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                vacations.size(),
                totalPages
        );
    }
}
