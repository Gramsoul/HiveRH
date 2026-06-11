package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
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

    // Listar todas las vacaciones
    @Transactional(readOnly = true)
    public List<VacationResponse> findAll() {

        List<VacationEntity> vacations = vacationRepository.findAll();

        return vacationMapper.toResponseList(vacations);
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
}