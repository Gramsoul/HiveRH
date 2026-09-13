package com.HiveGroup.HiveRH.Features.WorkRequest;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestCreateDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestResponseDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestReviewDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.WorkScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkRequestServiceTest {

    @Mock
    private WorkRequestRepository workRequestRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private WorkScheduleService workScheduleService;

    @InjectMocks
    private WorkRequestService workRequestService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createRejectsDuplicatePendingRequestForSameTypeAndDate() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        LocalDate targetDate = LocalDate.of(2026, 8, 21);
        authenticate(employeeAccount);

        WorkRequestCreateDTO request = new WorkRequestCreateDTO(
                WorkRequestType.DAY_OFF,
                targetDate,
                null,
                null,
                "Necesito el dia",
                null
        );
        WorkRequestEntity existing = workRequest(1L, employee, WorkRequestType.DAY_OFF, targetDate, RequestStatus.PENDING);

        when(workRequestRepository.findByEmployeeAndRequestTypeAndTargetDateAndStatus(
                employee,
                WorkRequestType.DAY_OFF,
                targetDate,
                RequestStatus.PENDING
        )).thenReturn(List.of(existing));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workRequestService.createCurrentEmployeeRequest(request)
        );

        assertEquals("El empleado ya tiene una solicitud pendiente del mismo tipo para esa fecha", exception.getMessage());
        verify(workRequestRepository, never()).save(any(WorkRequestEntity.class));
    }

    @Test
    void createRejectsShiftChangeWithoutTimeRange() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        authenticate(employeeAccount);

        WorkRequestCreateDTO request = new WorkRequestCreateDTO(
                WorkRequestType.SHIFT_CHANGE,
                LocalDate.of(2026, 8, 21),
                null,
                null,
                "Cambio de turno",
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workRequestService.createCurrentEmployeeRequest(request)
        );

        assertEquals("El horario de inicio y fin es obligatorio para ese tipo de solicitud", exception.getMessage());
        verify(workRequestRepository, never()).save(any(WorkRequestEntity.class));
    }

    @Test
    void createTrimsTextAndSavesPendingRequest() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        LocalDate targetDate = LocalDate.of(2026, 8, 21);
        authenticate(employeeAccount);

        WorkRequestCreateDTO request = new WorkRequestCreateDTO(
                WorkRequestType.EXTRA_HOURS,
                targetDate,
                LocalTime.of(18, 0),
                LocalTime.of(20, 0),
                "  Cierre de sprint  ",
                "  Compensar luego  "
        );

        when(workRequestRepository.findByEmployeeAndRequestTypeAndTargetDateAndStatus(
                employee,
                WorkRequestType.EXTRA_HOURS,
                targetDate,
                RequestStatus.PENDING
        )).thenReturn(List.of());
        when(workRequestRepository.save(any(WorkRequestEntity.class))).thenAnswer(invocation -> {
            WorkRequestEntity saved = invocation.getArgument(0);
            saved.setId_work_request(7L);
            return saved;
        });

        WorkRequestResponseDTO response = workRequestService.createCurrentEmployeeRequest(request);

        ArgumentCaptor<WorkRequestEntity> requestCaptor = ArgumentCaptor.forClass(WorkRequestEntity.class);
        verify(workRequestRepository).save(requestCaptor.capture());
        WorkRequestEntity savedRequest = requestCaptor.getValue();

        assertEquals(7L, response.idWorkRequest());
        assertEquals(RequestStatus.PENDING, savedRequest.getStatus());
        assertEquals("Cierre de sprint", savedRequest.getReason());
        assertEquals("Compensar luego", savedRequest.getConpensationDescription());
        assertEquals(employee, savedRequest.getEmployee());
    }

    @Test
    void cancelRejectsApprovedOwnRequest() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        WorkRequestEntity request = workRequest(
                1L,
                employee,
                WorkRequestType.DAY_OFF,
                LocalDate.of(2026, 8, 21),
                RequestStatus.APPROVED
        );
        authenticate(employeeAccount);

        when(workRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workRequestService.cancelCurrentEmployeeRequest(1L)
        );

        assertEquals("Solo se pueden cancelar solicitudes pendientes", exception.getMessage());
        verify(workRequestRepository, never()).save(any(WorkRequestEntity.class));
    }

    @Test
    void cancelRejectsAnotherEmployeeRequest() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        EmployeeEntity otherEmployee = employee(2L, "40222333", EmployeeStatus.ACTIVE);
        AccountEntity employeeAccount = linkedAccount(10L, "40111222", RolEnum.EMPLOYEE, employee);
        WorkRequestEntity request = workRequest(
                1L,
                otherEmployee,
                WorkRequestType.DAY_OFF,
                LocalDate.of(2026, 8, 21),
                RequestStatus.PENDING
        );
        authenticate(employeeAccount);

        when(workRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> workRequestService.cancelCurrentEmployeeRequest(1L)
        );

        assertEquals("No se puede cancelar una solicitud de otro empleado", exception.getMessage());
        verify(workRequestRepository, never()).save(any(WorkRequestEntity.class));
    }

    @Test
    void approveRecordsReviewerAndUpdatesSchedule() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        WorkRequestEntity request = workRequest(
                1L,
                employee,
                WorkRequestType.EXTRA_HOURS,
                LocalDate.of(2026, 8, 21),
                RequestStatus.PENDING
        );
        request.setStartTime(LocalTime.of(18, 0));
        request.setEndTime(LocalTime.of(20, 0));
        authenticate(admin);

        when(workRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(workRequestRepository.save(request)).thenReturn(request);

        WorkRequestResponseDTO response = workRequestService.approve(
                1L,
                new WorkRequestReviewDTO("  Aprobado por cobertura  ")
        );

        assertEquals(RequestStatus.APPROVED, request.getStatus());
        assertEquals(admin, request.getReviewedBy());
        assertEquals("Aprobado por cobertura", request.getReviewComment());
        assertEquals(RequestStatus.APPROVED, response.status());
        assertEquals(1L, response.reviewedByAccountId());
        assertEquals("admin", response.reviewedByUser());
        verify(workScheduleService).createFromApprovedRequest(request, admin);
        verify(workRequestRepository).save(request);
    }

    private void authenticate(AccountEntity account) {
        var authentication = new UsernamePasswordAuthenticationToken(
                account,
                null,
                account.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AccountEntity linkedAccount(Long id, String user, RolEnum rol, EmployeeEntity employee) {
        AccountEntity account = account(id, user, rol);
        account.setEmployee(employee);
        employee.setAccount(account);
        return account;
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

    private EmployeeEntity employee(Long id, String dni, EmployeeStatus status) {
        return EmployeeEntity.builder()
                .id_employee(id)
                .name("Ada")
                .lastName("Lovelace")
                .dni(dni)
                .hireDate(LocalDate.of(2026, 1, 10))
                .status(status)
                .build();
    }

    private WorkRequestEntity workRequest(
            Long id,
            EmployeeEntity employee,
            WorkRequestType type,
            LocalDate targetDate,
            RequestStatus status
    ) {
        return WorkRequestEntity.builder()
                .id_work_request(id)
                .employee(employee)
                .requestType(type)
                .requestDate(LocalDate.of(2026, 8, 18))
                .targetDate(targetDate)
                .reason("Motivo")
                .status(status)
                .build();
    }
}
