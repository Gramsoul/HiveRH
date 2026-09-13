package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.WorkRequest.WorkRequestEntity;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleFilterDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkSchedulePatchDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleRequestDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public WorkScheduleResponseDTO create(WorkScheduleRequestDTO request) {
        validateCreateRequest(request);

        EmployeeEntity employee = findEmployeeByDni(request.getDniEmployee());
        AccountEntity createdBy = getCurrentAccount();

        validateEmployeeCanHaveSchedule(employee, request.getWorkDate());
        validateScheduleData(request.getType(), request.getWorkDate(), request.getStartTime(), request.getEndTime());
        validateNoActiveOverlap(
                employee,
                request.getWorkDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getType(),
                null
        );

        WorkScheduleEntity schedule = WorkScheduleEntity.builder()
                .employee(employee)
                .workDate(request.getWorkDate())
                .startTime(resolveScheduleStartTime(request.getType(), request.getStartTime()))
                .endTime(resolveScheduleEndTime(request.getType(), request.getEndTime()))
                .type(request.getType())
                .status(WorkScheduleStatus.ACTIVE)
                .note(normalizeText(request.getNote()))
                .createdBy(createdBy)
                .build();

        return toResponse(workScheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<WorkScheduleResponseDTO> findAllByFilter(WorkScheduleFilterDTO filters, Pageable pageable) {
        WorkScheduleFilterDTO activeFilters = filters != null
                ? filters
                : new WorkScheduleFilterDTO(null, null, null, null, null, null, null);

        validateFilters(activeFilters);

        List<WorkScheduleResponseDTO> schedules = workScheduleRepository.findAll()
                .stream()
                .filter(schedule -> filterByEmployeeDni(schedule, activeFilters.dniEmployee()))
                .filter(schedule -> filterByBranch(schedule, activeFilters.branchId()))
                .filter(schedule -> filterByDepartment(schedule, activeFilters.departmentId()))
                .filter(schedule -> filterByDateRange(schedule, activeFilters.from(), activeFilters.to()))
                .filter(schedule -> activeFilters.type() == null || schedule.getType() == activeFilters.type())
                .filter(schedule -> activeFilters.status() == null || schedule.getStatus() == activeFilters.status())
                .map(this::toResponse)
                .toList();

        return toPageResponse(schedules, pageable);
    }

    @Transactional(readOnly = true)
    public List<WorkScheduleResponseDTO> findCurrentEmployeeSchedules(LocalDate from, LocalDate to) {
        validateDateRange(from, to);

        EmployeeEntity employee = findCurrentEmployee();

        return workScheduleRepository.findByEmployee(employee)
                .stream()
                .filter(schedule -> schedule.getStatus() == WorkScheduleStatus.ACTIVE)
                .filter(schedule -> filterByDateRange(schedule, from, to))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkScheduleResponseDTO findById(Long id) {
        return toResponse(findScheduleById(id));
    }

    @Transactional
    public WorkScheduleResponseDTO updateById(Long id, WorkSchedulePatchDTO request) {
        validatePatchRequest(request);

        WorkScheduleEntity schedule = findScheduleById(id);
        validateScheduleEditable(schedule);

        EmployeeEntity employee = schedule.getEmployee();
        LocalDate workDate = schedule.getWorkDate();
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        WorkScheduleType type = schedule.getType();

        if (request.getDniEmployee() != null) {
            employee = findEmployeeByDni(request.getDniEmployee());
        }

        if (request.getWorkDate() != null) {
            workDate = request.getWorkDate();
        }

        if (request.getStartTime() != null) {
            startTime = request.getStartTime();
        }

        if (request.getEndTime() != null) {
            endTime = request.getEndTime();
        }

        if (request.getType() != null) {
            type = request.getType();
        }

        if (type == WorkScheduleType.DAY_OFF || type == WorkScheduleType.HOLIDAY) {
            startTime = null;
            endTime = null;
        }

        validateEmployeeCanHaveSchedule(employee, workDate);
        validateScheduleData(type, workDate, startTime, endTime);
        validateNoActiveOverlap(employee, workDate, startTime, endTime, type, id);

        schedule.setEmployee(employee);
        schedule.setWorkDate(workDate);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setType(type);

        if (request.getNote() != null) {
            schedule.setNote(normalizeText(request.getNote()));
        }

        return toResponse(workScheduleRepository.save(schedule));
    }

    @Transactional
    public WorkScheduleResponseDTO cancel(Long id) {
        WorkScheduleEntity schedule = findScheduleById(id);

        if (schedule.getStatus() == WorkScheduleStatus.CANCELLED) {
            throw new IllegalArgumentException("El cronograma ya esta cancelado");
        }

        schedule.setStatus(WorkScheduleStatus.CANCELLED);

        return toResponse(workScheduleRepository.save(schedule));
    }

    @Transactional
    public WorkScheduleResponseDTO createFromApprovedRequest(WorkRequestEntity request, AccountEntity reviewer) {
        WorkScheduleType scheduleType = resolveScheduleType(request.getRequestType());

        validateEmployeeCanHaveSchedule(request.getEmployee(), request.getTargetDate());

        if (shouldReplaceDaySchedules(request.getRequestType())) {
            cancelActiveSchedulesForDate(request.getEmployee(), request.getTargetDate());
        }

        validateScheduleData(
                scheduleType,
                request.getTargetDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        validateNoActiveOverlap(
                request.getEmployee(),
                request.getTargetDate(),
                request.getStartTime(),
                request.getEndTime(),
                scheduleType,
                null
        );

        WorkScheduleEntity schedule = WorkScheduleEntity.builder()
                .employee(request.getEmployee())
                .workDate(request.getTargetDate())
                .startTime(resolveScheduleStartTime(scheduleType, request.getStartTime()))
                .endTime(resolveScheduleEndTime(scheduleType, request.getEndTime()))
                .type(scheduleType)
                .status(WorkScheduleStatus.ACTIVE)
                .note(buildNoteFromRequest(request))
                .createdBy(reviewer)
                .build();

        return toResponse(workScheduleRepository.save(schedule));
    }

    private void cancelActiveSchedulesForDate(EmployeeEntity employee, LocalDate workDate) {
        workScheduleRepository.findByEmployeeAndWorkDateAndStatus(employee, workDate, WorkScheduleStatus.ACTIVE)
                .forEach(schedule -> schedule.setStatus(WorkScheduleStatus.CANCELLED));
    }

    private WorkScheduleType resolveScheduleType(WorkRequestType requestType) {
        return switch (requestType) {
            case DAY_OFF, COMPENSATORY_DAY -> WorkScheduleType.DAY_OFF;
            case EXTRA_HOURS -> WorkScheduleType.EXTRA_HOURS;
            case SHIFT_CHANGE, LATE_ARRIVAL, EARLY_LEAVE -> WorkScheduleType.WORKDAY;
        };
    }

    private boolean shouldReplaceDaySchedules(WorkRequestType requestType) {
        return requestType == WorkRequestType.DAY_OFF
                || requestType == WorkRequestType.COMPENSATORY_DAY
                || requestType == WorkRequestType.SHIFT_CHANGE
                || requestType == WorkRequestType.LATE_ARRIVAL
                || requestType == WorkRequestType.EARLY_LEAVE;
    }

    private LocalTime resolveScheduleStartTime(WorkScheduleType type, LocalTime startTime) {
        return type == WorkScheduleType.DAY_OFF || type == WorkScheduleType.HOLIDAY ? null : startTime;
    }

    private LocalTime resolveScheduleEndTime(WorkScheduleType type, LocalTime endTime) {
        return type == WorkScheduleType.DAY_OFF || type == WorkScheduleType.HOLIDAY ? null : endTime;
    }

    private void validateCreateRequest(WorkScheduleRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de cronograma es obligatoria");
        }

        if (request.getDniEmployee() == null || request.getDniEmployee().isBlank()) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
    }

    private void validatePatchRequest(WorkSchedulePatchDTO request) {
        if (request == null || !request.isAnyFieldPresent()) {
            throw new IllegalArgumentException("Debe enviar al menos un campo para actualizar");
        }

        if (request.getDniEmployee() != null && request.getDniEmployee().isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacio");
        }
    }

    private void validateScheduleEditable(WorkScheduleEntity schedule) {
        if (schedule.getStatus() != WorkScheduleStatus.ACTIVE) {
            throw new IllegalArgumentException("Solo se pueden modificar cronogramas activos");
        }
    }

    private void validateScheduleData(
            WorkScheduleType type,
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (workDate == null) {
            throw new IllegalArgumentException("La fecha de trabajo es obligatoria");
        }

        if (type == null) {
            throw new IllegalArgumentException("El tipo de cronograma es obligatorio");
        }

        boolean requiresTimeRange = type == WorkScheduleType.WORKDAY || type == WorkScheduleType.EXTRA_HOURS;

        if (requiresTimeRange && (startTime == null || endTime == null)) {
            throw new IllegalArgumentException("El horario de inicio y fin es obligatorio para ese tipo de cronograma");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("El horario de fin debe ser posterior al horario de inicio");
        }
    }

    private void validateEmployeeCanHaveSchedule(EmployeeEntity employee, LocalDate workDate) {
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("No se puede asignar cronograma a un empleado que no esta activo");
        }

        if (employee.getHireDate() != null && workDate != null && workDate.isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("No se puede asignar cronograma antes de la fecha de contratacion");
        }

        if (employee.getTerminationDate() != null && workDate != null && workDate.isAfter(employee.getTerminationDate())) {
            throw new IllegalArgumentException("No se puede asignar cronograma despues de la fecha de baja");
        }
    }

    private void validateNoActiveOverlap(
            EmployeeEntity employee,
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime,
            WorkScheduleType type,
            Long idScheduleToIgnore
    ) {
        boolean overlaps = workScheduleRepository
                .findByEmployeeAndWorkDateAndStatus(employee, workDate, WorkScheduleStatus.ACTIVE)
                .stream()
                .filter(schedule -> idScheduleToIgnore == null
                        || !schedule.getId_work_schedule().equals(idScheduleToIgnore))
                .anyMatch(schedule -> schedulesOverlap(schedule, startTime, endTime, type));

        if (overlaps) {
            throw new IllegalArgumentException("El empleado ya tiene un cronograma activo superpuesto para esa fecha y horario");
        }
    }

    private boolean schedulesOverlap(
            WorkScheduleEntity existing,
            LocalTime newStartTime,
            LocalTime newEndTime,
            WorkScheduleType newType
    ) {
        boolean existingCoversDay = existing.getStartTime() == null || existing.getEndTime() == null
                || existing.getType() == WorkScheduleType.DAY_OFF
                || existing.getType() == WorkScheduleType.HOLIDAY;
        boolean newCoversDay = newStartTime == null || newEndTime == null
                || newType == WorkScheduleType.DAY_OFF
                || newType == WorkScheduleType.HOLIDAY;

        if (existingCoversDay || newCoversDay) {
            return true;
        }

        return existing.getStartTime().isBefore(newEndTime)
                && newStartTime.isBefore(existing.getEndTime());
    }

    private void validateFilters(WorkScheduleFilterDTO filters) {
        validateDateRange(filters.from(), filters.to());

        if (filters.dniEmployee() != null && filters.dniEmployee().isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacio");
        }

        if (filters.branchId() != null && filters.branchId() <= 0) {
            throw new IllegalArgumentException("El ID de sucursal debe ser mayor que cero");
        }

        if (filters.departmentId() != null && filters.departmentId() <= 0) {
            throw new IllegalArgumentException("El ID de departamento debe ser mayor que cero");
        }
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
    }

    private boolean filterByEmployeeDni(WorkScheduleEntity schedule, String dniEmployee) {
        return dniEmployee == null
                || dniEmployee.isBlank()
                || schedule.getEmployee().getDni().equals(dniEmployee);
    }

    private boolean filterByBranch(WorkScheduleEntity schedule, Long branchId) {
        return branchId == null || hasAssignmentForDate(schedule.getEmployee(), branchId, null, schedule.getWorkDate());
    }

    private boolean filterByDepartment(WorkScheduleEntity schedule, Long departmentId) {
        return departmentId == null || hasAssignmentForDate(schedule.getEmployee(), null, departmentId, schedule.getWorkDate());
    }

    private boolean hasAssignmentForDate(
            EmployeeEntity employee,
            Long branchId,
            Long departmentId,
            LocalDate referenceDate
    ) {
        if (employee.getAssignments() == null) {
            return false;
        }

        return employee.getAssignments()
                .stream()
                .filter(assignment -> isAssignmentEffectiveOn(assignment, referenceDate))
                .anyMatch(assignment -> (branchId == null
                        || Objects.equals(assignment.getBranch().getId_branch(), branchId))
                        && (departmentId == null
                        || Objects.equals(assignment.getDepartment().getId_department(), departmentId)));
    }

    private boolean isAssignmentEffectiveOn(EmployeeAssignmentEntity assignment, LocalDate referenceDate) {
        if (referenceDate == null) {
            return assignment.isActive();
        }

        boolean started = assignment.getStartDate() == null || !assignment.getStartDate().isAfter(referenceDate);
        boolean notEnded = assignment.getEndDate() == null || !assignment.getEndDate().isBefore(referenceDate);

        return started && notEnded;
    }

    private boolean filterByDateRange(WorkScheduleEntity schedule, LocalDate from, LocalDate to) {
        return (from == null || !schedule.getWorkDate().isBefore(from))
                && (to == null || !schedule.getWorkDate().isAfter(to));
    }

    private EmployeeEntity findEmployeeByDni(String dniEmployee) {
        return employeeRepository.findByDni(dniEmployee)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado", "Employee"));
    }

    private WorkScheduleEntity findScheduleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El cronograma es obligatorio");
        }

        return workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cronograma no encontrado", "WorkSchedule"));
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

    private String normalizeText(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private String buildNoteFromRequest(WorkRequestEntity request) {
        String reason = normalizeText(request.getReason());
        String compensation = normalizeText(request.getConpensationDescription());
        String note = "Solicitud aprobada #" + request.getId_work_request();

        if (reason != null) {
            note += ": " + reason;
        }

        if (compensation != null) {
            note += " - " + compensation;
        }

        return note.length() > 500 ? note.substring(0, 500) : note;
    }

    private WorkScheduleResponseDTO toResponse(WorkScheduleEntity schedule) {
        return new WorkScheduleResponseDTO(
                schedule.getId_work_schedule(),
                schedule.getEmployee().getDni(),
                schedule.getEmployee().getName() + " " + schedule.getEmployee().getLastName(),
                schedule.getWorkDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getType(),
                schedule.getStatus(),
                schedule.getNote(),
                schedule.getCreatedBy() != null ? schedule.getCreatedBy().getId_account() : null,
                schedule.getCreatedBy() != null ? schedule.getCreatedBy().getUser() : null
        );
    }

    private PageResponseDTO<WorkScheduleResponseDTO> toPageResponse(
            List<WorkScheduleResponseDTO> schedules,
            Pageable pageable
    ) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    schedules,
                    0,
                    schedules.size(),
                    schedules.size(),
                    schedules.isEmpty() ? 0 : 1
            );
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, schedules.size());
        List<WorkScheduleResponseDTO> content = start >= schedules.size()
                ? List.of()
                : schedules.subList(start, end);

        int totalPages = (int) Math.ceil((double) schedules.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                schedules.size(),
                totalPages
        );
    }
}
