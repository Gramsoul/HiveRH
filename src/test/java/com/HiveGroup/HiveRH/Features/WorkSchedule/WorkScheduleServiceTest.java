package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AccountStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkSchedulePatchDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleRequestDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleServiceTest {

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private WorkScheduleService workScheduleService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createRejectsOverlappingActiveSchedule() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        LocalDate workDate = LocalDate.of(2026, 8, 18);
        authenticate(admin);

        WorkScheduleEntity existing = schedule(
                5L,
                employee,
                workDate,
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                WorkScheduleType.WORKDAY,
                WorkScheduleStatus.ACTIVE,
                admin
        );
        WorkScheduleRequestDTO request = WorkScheduleRequestDTO.builder()
                .dniEmployee("40111222")
                .workDate(workDate)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(16, 0))
                .type(WorkScheduleType.WORKDAY)
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(workScheduleRepository.findByEmployeeAndWorkDateAndStatus(employee, workDate, WorkScheduleStatus.ACTIVE))
                .thenReturn(List.of(existing));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workScheduleService.create(request)
        );

        assertEquals("El empleado ya tiene un cronograma activo superpuesto para esa fecha y horario", exception.getMessage());
        verify(workScheduleRepository, never()).save(any(WorkScheduleEntity.class));
    }

    @Test
    void createRejectsWorkdayWithoutTimeRange() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        authenticate(admin);

        WorkScheduleRequestDTO request = WorkScheduleRequestDTO.builder()
                .dniEmployee("40111222")
                .workDate(LocalDate.of(2026, 8, 18))
                .type(WorkScheduleType.WORKDAY)
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workScheduleService.create(request)
        );

        assertEquals("El horario de inicio y fin es obligatorio para ese tipo de cronograma", exception.getMessage());
        verify(workScheduleRepository, never()).save(any(WorkScheduleEntity.class));
    }

    @Test
    void createNormalizesDayOffTimesToNull() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        LocalDate workDate = LocalDate.of(2026, 8, 18);
        authenticate(admin);

        WorkScheduleRequestDTO request = WorkScheduleRequestDTO.builder()
                .dniEmployee("40111222")
                .workDate(workDate)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(14, 0))
                .type(WorkScheduleType.DAY_OFF)
                .note("  Franco compensatorio  ")
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));
        when(workScheduleRepository.findByEmployeeAndWorkDateAndStatus(employee, workDate, WorkScheduleStatus.ACTIVE))
                .thenReturn(List.of());
        when(workScheduleRepository.save(any(WorkScheduleEntity.class))).thenAnswer(invocation -> {
            WorkScheduleEntity saved = invocation.getArgument(0);
            saved.setId_work_schedule(9L);
            return saved;
        });

        WorkScheduleResponseDTO response = workScheduleService.create(request);

        ArgumentCaptor<WorkScheduleEntity> scheduleCaptor = ArgumentCaptor.forClass(WorkScheduleEntity.class);
        verify(workScheduleRepository).save(scheduleCaptor.capture());
        WorkScheduleEntity savedSchedule = scheduleCaptor.getValue();

        assertEquals(9L, response.idWorkSchedule());
        assertEquals(WorkScheduleType.DAY_OFF, savedSchedule.getType());
        assertNull(savedSchedule.getStartTime());
        assertNull(savedSchedule.getEndTime());
        assertEquals("Franco compensatorio", savedSchedule.getNote());
        assertEquals(admin, savedSchedule.getCreatedBy());
    }

    @Test
    void createRejectsInactiveEmployee() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.TERMINATED);
        authenticate(admin);

        WorkScheduleRequestDTO request = WorkScheduleRequestDTO.builder()
                .dniEmployee("40111222")
                .workDate(LocalDate.of(2026, 8, 18))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(14, 0))
                .type(WorkScheduleType.WORKDAY)
                .build();

        when(employeeRepository.findByDni("40111222")).thenReturn(Optional.of(employee));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workScheduleService.create(request)
        );

        assertEquals("No se puede asignar cronograma a un empleado que no esta activo", exception.getMessage());
        verify(workScheduleRepository, never()).save(any(WorkScheduleEntity.class));
    }

    @Test
    void updateRejectsCancelledSchedule() {
        AccountEntity admin = account(1L, "admin", RolEnum.ADMIN);
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        WorkScheduleEntity cancelled = schedule(
                1L,
                employee,
                LocalDate.of(2026, 8, 18),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                WorkScheduleType.WORKDAY,
                WorkScheduleStatus.CANCELLED,
                admin
        );
        WorkSchedulePatchDTO patch = WorkSchedulePatchDTO.builder()
                .note("Nuevo comentario")
                .build();

        when(workScheduleRepository.findById(1L)).thenReturn(Optional.of(cancelled));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> workScheduleService.updateById(1L, patch)
        );

        assertEquals("Solo se pueden modificar cronogramas activos", exception.getMessage());
        verify(workScheduleRepository, never()).save(any(WorkScheduleEntity.class));
    }

    @Test
    void staffWithLinkedEmployeeCanReadOwnActiveSchedulesInRange() {
        EmployeeEntity employee = employee(1L, "40111222", EmployeeStatus.ACTIVE);
        AccountEntity staff = account(10L, "staff.demo", RolEnum.STAFF);
        staff.setEmployee(employee);
        employee.setAccount(staff);
        authenticate(staff);

        WorkScheduleEntity activeInRange = schedule(
                1L,
                employee,
                LocalDate.of(2026, 8, 18),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                WorkScheduleType.WORKDAY,
                WorkScheduleStatus.ACTIVE,
                staff
        );
        WorkScheduleEntity cancelledInRange = schedule(
                2L,
                employee,
                LocalDate.of(2026, 8, 19),
                null,
                null,
                WorkScheduleType.DAY_OFF,
                WorkScheduleStatus.CANCELLED,
                staff
        );
        WorkScheduleEntity activeOutOfRange = schedule(
                3L,
                employee,
                LocalDate.of(2026, 9, 1),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                WorkScheduleType.WORKDAY,
                WorkScheduleStatus.ACTIVE,
                staff
        );

        when(workScheduleRepository.findByEmployee(employee))
                .thenReturn(List.of(activeInRange, cancelledInRange, activeOutOfRange));

        List<WorkScheduleResponseDTO> response = workScheduleService.findCurrentEmployeeSchedules(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31)
        );

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).idWorkSchedule());
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

    private WorkScheduleEntity schedule(
            Long id,
            EmployeeEntity employee,
            LocalDate workDate,
            LocalTime startTime,
            LocalTime endTime,
            WorkScheduleType type,
            WorkScheduleStatus status,
            AccountEntity createdBy
    ) {
        return WorkScheduleEntity.builder()
                .id_work_schedule(id)
                .employee(employee)
                .workDate(workDate)
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .status(status)
                .createdBy(createdBy)
                .build();
    }
}
