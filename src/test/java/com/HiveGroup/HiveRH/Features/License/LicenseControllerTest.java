package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
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
class LicenseControllerTest {

    @Mock
    private LicenseService licenseService;

    @InjectMocks
    private LicenseController licenseController;

    @Test
    void getMyLicensesReturnsCurrentEmployeeLicenses() {
        LocalDate startDate = LocalDate.of(2026, 11, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 30);
        PageRequest pageable = PageRequest.of(0, 10);
        PageResponseDTO<LicenseDTO> page = new PageResponseDTO<>(
                List.of(LicenseDTO.builder().id(1L).status(AbsenceStatus.PENDING).build()),
                0,
                10,
                1,
                1
        );

        when(licenseService.getCurrentEmployeeLicenses(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                false,
                pageable
        )).thenReturn(page);

        var response = licenseController.getMyLicenses(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                false,
                pageable
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(licenseService).getCurrentEmployeeLicenses(
                AbsenceStatus.PENDING,
                startDate,
                endDate,
                false,
                pageable
        );
    }
}
