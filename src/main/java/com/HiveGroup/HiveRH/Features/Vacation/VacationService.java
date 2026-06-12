package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Security.Config.SecurityAuthorizationService;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Common.Utils.TextSearchUtils;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationFilterDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacationService {

    private final VacationRepository vacationRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationMapper vacationMapper;
    private final SecurityAuthorizationService securityAuthorizationService;

    // Crear vacaciones
    @Transactional
    public VacationResponse create(VacationRequest request) {

        // Valida que los datos obligatorios del request.
        validateRequest(request);

        EmployeeEntity employee = findEmployeeById(request.idEmployee());

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
    public List<VacationResponse> findAllByFilter(VacationFilterDTO filters) {

        VacationFilterDTO activeFilters = filters != null
                ? filters
                : new VacationFilterDTO(null, null, null, null, null);

        validateFilterDateRange(activeFilters);

        return vacationRepository.findAll()
                .stream()
                .filter(vacation -> filterById(vacation, activeFilters.idVacation()))
                .filter(vacation -> filterByAccepted(vacation, activeFilters.accepted()))
                .filter(vacation -> filterByDateRange(vacation, activeFilters))
                .filter(vacation -> filterByEmployeeFullName(vacation, activeFilters.fullName()))
                .map(vacationMapper::toResponse)
                .toList();
    }

    // Actualizar vacaciones
    @Transactional
    public VacationResponse updateById(Long idVacation, VacationRequest request) {

        validateRequest(request);

        VacationEntity vacation = findVacationById(idVacation);

        EmployeeEntity employee = findEmployeeById(request.idEmployee());

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

        vacation.setAccepted(request.accepted());
        vacation.setStartDate(request.startDate());
        vacation.setEndDate(request.endDate());
        vacation.setPaid(request.paid());
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
    private EmployeeEntity findEmployeeById(Long idEmployee) {

        return employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Empleado no encontrado",
                        "Employee"
                ));
    }

    // Validar datos obligatorios
    private void validateRequest(VacationRequest request) {

        if (request.idEmployee() == null) {
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

        if (employee.getStatus() != StatusEnum.ACTIVE) {
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

    private boolean filterById(VacationEntity vacation, Long idVacation) {

        return idVacation == null || vacation.getId_vacation().equals(idVacation);
    }

    private boolean filterByAccepted(VacationEntity vacation, Boolean accepted) {

        return accepted == null || vacation.isAccepted() == accepted;
    }

    private boolean filterByDateRange(VacationEntity vacation, VacationFilterDTO filters) {

        if (filters.startDate() == null && filters.endDate() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.endDate() == null || !vacation.getStartDate().isAfter(filters.endDate());
        boolean endsAfterFilterStart = filters.startDate() == null || !vacation.getEndDate().isBefore(filters.startDate());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private boolean filterByEmployeeFullName(VacationEntity vacation, String fullName) {

        return TextSearchUtils.matchesFullName(
                vacation.getEmployee().getName(),
                vacation.getEmployee().getLastName(),
                fullName
        );
    }
}
