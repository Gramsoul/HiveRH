package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleFilterDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkSchedulePatchDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleRequestDTO;
import com.HiveGroup.HiveRH.Features.WorkSchedule.DTO.WorkScheduleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedules")
@AllArgsConstructor
@Tag(name = "07 Work Schedules", description = "Assigned employee work schedules and daily work blocks.")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @GetMapping("/me")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "List my work schedules", description = "Returns active work schedules for the employee linked to the authenticated account.")
    public ResponseEntity<List<WorkScheduleResponseDTO>> getMyWorkSchedules(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return ResponseEntity.ok(workScheduleService.findCurrentEmployeeSchedules(from, to));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List work schedules", description = "Returns paginated work schedules filtered by employee, branch, department, date range, type, or status.")
    public ResponseEntity<PageResponseDTO<WorkScheduleResponseDTO>> getWorkSchedules(
            @ParameterObject WorkScheduleFilterDTO filters,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(workScheduleService.findAllByFilter(filters, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get work schedule", description = "Returns a work schedule by ID.")
    public ResponseEntity<WorkScheduleResponseDTO> getWorkSchedule(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(workScheduleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create work schedule", description = "Creates an active work schedule for an employee identified by DNI.")
    public ResponseEntity<WorkScheduleResponseDTO> createWorkSchedule(
            @Valid @RequestBody WorkScheduleRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workScheduleService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Update work schedule", description = "Updates an active work schedule and prevents overlapping active schedules.")
    public ResponseEntity<WorkScheduleResponseDTO> patchWorkSchedule(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody WorkSchedulePatchDTO request
    ) {
        return ResponseEntity.ok(workScheduleService.updateById(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cancel work schedule", description = "Cancels a work schedule without physically deleting it.")
    public ResponseEntity<WorkScheduleResponseDTO> cancelWorkSchedule(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(workScheduleService.cancel(id));
    }
}
