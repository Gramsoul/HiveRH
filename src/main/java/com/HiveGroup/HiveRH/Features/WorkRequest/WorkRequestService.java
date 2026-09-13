package com.HiveGroup.HiveRH.Features.WorkRequest;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentEntity;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestCreateDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestFilterDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestResponseDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestReviewDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.WorkScheduleService;
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
public class WorkRequestService {

    private final WorkRequestRepository workRequestRepository;
    private final AccountRepository accountRepository;
    private final WorkScheduleService workScheduleService;

    @Transactional
    public WorkRequestResponseDTO createCurrentEmployeeRequest(WorkRequestCreateDTO request) {
        validateCreateRequest(request);

        EmployeeEntity employee = findCurrentEmployee();

        validateEmployeeCanCreateRequest(employee, request.targetDate());
        validateTimeRangeForRequestType(request.requestType(), request.startTime(), request.endTime());
        validateEmployeeHasNoPendingRequestForSameTypeAndDate(employee, request.requestType(), request.targetDate());

        WorkRequestEntity workRequest = WorkRequestEntity.builder()
                .employee(employee)
                .requestType(request.requestType())
                .requestDate(LocalDate.now())
                .targetDate(request.targetDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .reason(normalizeText(request.reason()))
                .conpensationDescription(normalizeText(request.compensationDescription()))
                .status(RequestStatus.PENDING)
                .build();

        return toResponse(workRequestRepository.save(workRequest));
    }

    @Transactional(readOnly = true)
    public List<WorkRequestResponseDTO> findCurrentEmployeeRequests(WorkRequestFilterDTO filters) {
        WorkRequestFilterDTO activeFilters = filters != null
                ? filters
                : new WorkRequestFilterDTO(null, null, null, null, null, null, null);

        validateDateRange(activeFilters.from(), activeFilters.to());

        EmployeeEntity employee = findCurrentEmployee();

        return workRequestRepository.findByEmployee(employee)
                .stream()
                .filter(request -> filterByDateRange(request, activeFilters.from(), activeFilters.to()))
                .filter(request -> activeFilters.requestType() == null || request.getRequestType() == activeFilters.requestType())
                .filter(request -> activeFilters.status() == null || request.getStatus() == activeFilters.status())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkRequestResponseDTO findCurrentEmployeeRequestById(Long id) {
        EmployeeEntity employee = findCurrentEmployee();
        WorkRequestEntity request = findRequestById(id);

        if (!request.getEmployee().getId_employee().equals(employee.getId_employee())) {
            throw new AccessDeniedException("No se puede consultar una solicitud de otro empleado");
        }

        return toResponse(request);
    }

    @Transactional
    public WorkRequestResponseDTO cancelCurrentEmployeeRequest(Long id) {
        EmployeeEntity employee = findCurrentEmployee();
        WorkRequestEntity request = findRequestById(id);

        if (!request.getEmployee().getId_employee().equals(employee.getId_employee())) {
            throw new AccessDeniedException("No se puede cancelar una solicitud de otro empleado");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden cancelar solicitudes pendientes");
        }

        request.setStatus(RequestStatus.CANCELLED);

        return toResponse(workRequestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<WorkRequestResponseDTO> findAllByFilter(WorkRequestFilterDTO filters, Pageable pageable) {
        WorkRequestFilterDTO activeFilters = filters != null
                ? filters
                : new WorkRequestFilterDTO(null, null, null, null, null, null, null);

        validateFilters(activeFilters);

        List<WorkRequestResponseDTO> requests = workRequestRepository.findAll()
                .stream()
                .filter(request -> filterByEmployeeDni(request, activeFilters.dniEmployee()))
                .filter(request -> filterByBranch(request, activeFilters.branchId()))
                .filter(request -> filterByDepartment(request, activeFilters.departmentId()))
                .filter(request -> filterByDateRange(request, activeFilters.from(), activeFilters.to()))
                .filter(request -> activeFilters.requestType() == null || request.getRequestType() == activeFilters.requestType())
                .filter(request -> activeFilters.status() == null || request.getStatus() == activeFilters.status())
                .map(this::toResponse)
                .toList();

        return toPageResponse(requests, pageable);
    }

    @Transactional(readOnly = true)
    public WorkRequestResponseDTO findById(Long id) {
        return toResponse(findRequestById(id));
    }

    @Transactional
    public WorkRequestResponseDTO approve(Long id, WorkRequestReviewDTO review) {
        WorkRequestEntity request = findRequestById(id);
        validatePendingRequest(request);

        AccountEntity reviewer = getCurrentAccount();
        workScheduleService.createFromApprovedRequest(request, reviewer);

        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedBy(reviewer);
        request.setReviewComment(normalizeText(review != null ? review.reviewComment() : null));

        return toResponse(workRequestRepository.save(request));
    }

    @Transactional
    public WorkRequestResponseDTO reject(Long id, WorkRequestReviewDTO review) {
        WorkRequestEntity request = findRequestById(id);
        validatePendingRequest(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedBy(getCurrentAccount());
        request.setReviewComment(normalizeText(review != null ? review.reviewComment() : null));

        return toResponse(workRequestRepository.save(request));
    }

    private void validateCreateRequest(WorkRequestCreateDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud laboral es obligatoria");
        }

        if (request.requestType() == null) {
            throw new IllegalArgumentException("El tipo de solicitud es obligatorio");
        }

        if (request.targetDate() == null) {
            throw new IllegalArgumentException("La fecha objetivo es obligatoria");
        }
    }

    private void validateEmployeeCanCreateRequest(EmployeeEntity employee, LocalDate targetDate) {
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("No se pueden crear solicitudes para un empleado que no esta activo");
        }

        if (employee.getHireDate() != null && targetDate.isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("No se puede crear una solicitud anterior a la fecha de contratacion");
        }

        if (employee.getTerminationDate() != null && targetDate.isAfter(employee.getTerminationDate())) {
            throw new IllegalArgumentException("No se puede crear una solicitud posterior a la fecha de baja");
        }
    }

    private void validateTimeRangeForRequestType(
            WorkRequestType requestType,
            LocalTime startTime,
            LocalTime endTime
    ) {
        boolean requiresTimeRange = requestType == WorkRequestType.SHIFT_CHANGE
                || requestType == WorkRequestType.EXTRA_HOURS
                || requestType == WorkRequestType.LATE_ARRIVAL
                || requestType == WorkRequestType.EARLY_LEAVE;

        if (requiresTimeRange && (startTime == null || endTime == null)) {
            throw new IllegalArgumentException("El horario de inicio y fin es obligatorio para ese tipo de solicitud");
        }

        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("El horario de fin debe ser posterior al horario de inicio");
        }
    }

    private void validateEmployeeHasNoPendingRequestForSameTypeAndDate(
            EmployeeEntity employee,
            WorkRequestType requestType,
            LocalDate targetDate
    ) {
        boolean exists = !workRequestRepository
                .findByEmployeeAndRequestTypeAndTargetDateAndStatus(
                        employee,
                        requestType,
                        targetDate,
                        RequestStatus.PENDING
                )
                .isEmpty();

        if (exists) {
            throw new IllegalArgumentException("El empleado ya tiene una solicitud pendiente del mismo tipo para esa fecha");
        }
    }

    private void validatePendingRequest(WorkRequestEntity request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Solo se pueden revisar solicitudes pendientes");
        }
    }

    private void validateFilters(WorkRequestFilterDTO filters) {
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

    private boolean filterByEmployeeDni(WorkRequestEntity request, String dniEmployee) {
        return dniEmployee == null
                || dniEmployee.isBlank()
                || request.getEmployee().getDni().equals(dniEmployee);
    }

    private boolean filterByBranch(WorkRequestEntity request, Long branchId) {
        return branchId == null || hasAssignmentForDate(request.getEmployee(), branchId, null, request.getTargetDate());
    }

    private boolean filterByDepartment(WorkRequestEntity request, Long departmentId) {
        return departmentId == null || hasAssignmentForDate(request.getEmployee(), null, departmentId, request.getTargetDate());
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

    private boolean filterByDateRange(WorkRequestEntity request, LocalDate from, LocalDate to) {
        return (from == null || !request.getTargetDate().isBefore(from))
                && (to == null || !request.getTargetDate().isAfter(to));
    }

    private EmployeeEntity findCurrentEmployee() {
        AccountEntity account = getCurrentAccount();

        if (account.getEmployee() == null) {
            throw new EntityNotFoundException("Empleado no encontrado para la cuenta autenticada", "Employee");
        }

        return account.getEmployee();
    }

    private WorkRequestEntity findRequestById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("La solicitud laboral es obligatoria");
        }

        return workRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud laboral no encontrada", "WorkRequest"));
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

    private WorkRequestResponseDTO toResponse(WorkRequestEntity request) {
        return new WorkRequestResponseDTO(
                request.getId_work_request(),
                request.getEmployee().getDni(),
                request.getEmployee().getName() + " " + request.getEmployee().getLastName(),
                request.getRequestType(),
                request.getRequestDate(),
                request.getTargetDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getReason(),
                request.getConpensationDescription(),
                request.getStatus(),
                request.getReviewedBy() != null ? request.getReviewedBy().getId_account() : null,
                request.getReviewedBy() != null ? request.getReviewedBy().getUser() : null,
                request.getReviewComment()
        );
    }

    private PageResponseDTO<WorkRequestResponseDTO> toPageResponse(
            List<WorkRequestResponseDTO> requests,
            Pageable pageable
    ) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageResponseDTO<>(
                    requests,
                    0,
                    requests.size(),
                    requests.size(),
                    requests.isEmpty() ? 0 : 1
            );
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + size, requests.size());
        List<WorkRequestResponseDTO> content = start >= requests.size()
                ? List.of()
                : requests.subList(start, end);

        int totalPages = (int) Math.ceil((double) requests.size() / size);

        return new PageResponseDTO<>(
                content,
                page,
                size,
                requests.size(),
                totalPages
        );
    }
}
