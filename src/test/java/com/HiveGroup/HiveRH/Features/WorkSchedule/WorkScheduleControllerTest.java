package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkSchedulePatchDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleRequestDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkScheduleControllerTest {

    @Mock
    private WorkScheduleService workScheduleService;

    @InjectMocks
    private WorkScheduleController workScheduleController;

    @Test
    void getMyWorkSchedules() {
        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 31);
        List<WorkScheduleResponseDTO> schedules = List.of(workScheduleResponse());

        when(workScheduleService.findCurrentEmployeeSchedules(from, to)).thenReturn(schedules);

        var response = workScheduleController.getMyWorkSchedules(from, to);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedules, response.getBody());
        verify(workScheduleService).findCurrentEmployeeSchedules(from, to);
    }

    @Test
    void getWorkSchedules() {
        Pageable pageable = Pageable.unpaged();
        PageResponseDTO<WorkScheduleResponseDTO> page = new PageResponseDTO<>(
                List.of(workScheduleResponse()),
                0,
                1,
                1,
                1
        );

        when(workScheduleService.findAllByFilter(null, pageable)).thenReturn(page);

        var response = workScheduleController.getWorkSchedules(null, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(workScheduleService).findAllByFilter(null, pageable);
    }

    @Test
    void getWorkSchedule() {
        WorkScheduleResponseDTO schedule = workScheduleResponse();

        when(workScheduleService.findById(1L)).thenReturn(schedule);

        var response = workScheduleController.getWorkSchedule(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedule, response.getBody());
        verify(workScheduleService).findById(1L);
    }

    @Test
    void createWorkSchedule() {
        WorkScheduleRequestDTO request = WorkScheduleRequestDTO.builder()
                .dniEmployee("40111222")
                .workDate(LocalDate.of(2026, 8, 18))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(14, 0))
                .type(WorkScheduleType.WORKDAY)
                .build();
        WorkScheduleResponseDTO schedule = workScheduleResponse();

        when(workScheduleService.create(request)).thenReturn(schedule);

        var response = workScheduleController.createWorkSchedule(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(schedule, response.getBody());
        verify(workScheduleService).create(request);
    }

    @Test
    void patchWorkSchedule() {
        WorkSchedulePatchDTO request = WorkSchedulePatchDTO.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(15, 0))
                .build();
        WorkScheduleResponseDTO schedule = workScheduleResponse();

        when(workScheduleService.updateById(1L, request)).thenReturn(schedule);

        var response = workScheduleController.patchWorkSchedule(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedule, response.getBody());
        verify(workScheduleService).updateById(1L, request);
    }

    @Test
    void cancelWorkSchedule() {
        WorkScheduleResponseDTO schedule = workScheduleResponse();

        when(workScheduleService.cancel(1L)).thenReturn(schedule);

        var response = workScheduleController.cancelWorkSchedule(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(schedule, response.getBody());
        verify(workScheduleService).cancel(1L);
    }

    private WorkScheduleResponseDTO workScheduleResponse() {
        return new WorkScheduleResponseDTO(
                1L,
                "40111222",
                "Ada Lovelace",
                LocalDate.of(2026, 8, 18),
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                WorkScheduleType.WORKDAY,
                WorkScheduleStatus.ACTIVE,
                "Turno manana",
                10L,
                "admin"
        );
    }
}
