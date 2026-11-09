package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationControllerTest {

    @Mock
    private VacationService vacationService;

    @InjectMocks
    private VacationController vacationController;

    @Test
    void findMineReturnsCurrentEmployeeVacations() {
        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 31);
        PageRequest pageable = PageRequest.of(0, 10);
        VacationResponse vacation = vacationResponse();
        PageResponseDTO<VacationResponse> page = new PageResponseDTO<>(
                List.of(vacation),
                0,
                10,
                1,
                1
        );

        when(vacationService.findCurrentEmployeeVacations(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                pageable
        )).thenReturn(page);

        var response = vacationController.findMine(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                pageable
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(vacationService).findCurrentEmployeeVacations(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                pageable
        );
    }

    private VacationResponse vacationResponse() {
        return new VacationResponse(
                1L,
                LocalDate.of(2026, 9, 1),
                AbsenceStatus.PENDING,
                LocalDate.of(2026, 10, 12),
                LocalDate.of(2026, 10, 16),
                null,
                null,
                "40111222",
                "Ada Lovelace"
        );
    }
}
