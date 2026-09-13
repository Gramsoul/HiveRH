package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationFilterDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
@Tag(name = "12 Vacations", description = "Vacation requests and records.")
public class VacationController {

    private final VacationService vacationService;

    @GetMapping
    @Operation(summary = "List vacations", description = "Returns paginated vacations and supports filtering by status, date range, DNI, and full name.")
    public ResponseEntity<PageResponseDTO<VacationResponse>> findAll(
            @ParameterObject VacationFilterDTO filters,
            @ParameterObject Pageable pageable) {

        PageResponseDTO<VacationResponse> response = vacationService.findAllByFilter(filters, pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.canCreateVacationForEmployeeDni(#request.dniEmployee())")
    @Operation(summary = "Create vacation", description = "Registers vacation days for an active employee. Validates dates and prevents overlapping vacations for the same employee.")
    public ResponseEntity<VacationResponse> create(@P("request") @Valid @RequestBody VacationRequest request) {

        VacationResponse response = vacationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id_vacation}")
    @Operation(summary = "Update vacation", description = "Updates an existing vacation record.")
    public ResponseEntity<VacationResponse> updateById(
            @PathVariable("id_vacation") Long idVacation,
            @Valid @RequestBody VacationRequest request
    ) {

        VacationResponse response = vacationService.updateById(idVacation, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id_vacation}")
    @PreAuthorize("@securityAuthorizationService.canDeleteVacation(#idVacation)")
    @Operation(summary = "Delete vacation", description = "Deletes the selected vacation record. Authorization validates whether the user can delete that request.")
    public ResponseEntity<VacationResponse> deleteById(
            @P("idVacation") @PathVariable("id_vacation") Long idVacation
    ) {

        VacationResponse response = vacationService.deleteById(idVacation);

        return ResponseEntity.ok(response);
    }
}
