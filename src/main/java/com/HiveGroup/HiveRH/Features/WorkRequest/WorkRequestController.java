package com.HiveGroup.HiveRH.Features.WorkRequest;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestCreateDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestFilterDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestResponseDTO;
import com.HiveGroup.HiveRH.Features.WorkRequest.DTO.WorkRequestReviewDTO;
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

import java.util.List;

@RestController
@RequestMapping("/api/work-requests")
@AllArgsConstructor
@Tag(name = "08 Work Requests", description = "Employee daily work requests and administrative review.")
public class WorkRequestController {

    private final WorkRequestService workRequestService;

    @PostMapping("/me")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "Create my work request", description = "Creates a PENDING work request for the employee linked to the authenticated account.")
    public ResponseEntity<WorkRequestResponseDTO> createMyWorkRequest(
            @Valid @RequestBody WorkRequestCreateDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workRequestService.createCurrentEmployeeRequest(request));
    }

    @GetMapping("/me")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "List my work requests", description = "Returns work requests created by the employee linked to the authenticated account.")
    public ResponseEntity<List<WorkRequestResponseDTO>> getMyWorkRequests(
            @ParameterObject WorkRequestFilterDTO filters
    ) {
        return ResponseEntity.ok(workRequestService.findCurrentEmployeeRequests(filters));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "Get my work request", description = "Returns one work request owned by the employee linked to the authenticated account.")
    public ResponseEntity<WorkRequestResponseDTO> getMyWorkRequest(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(workRequestService.findCurrentEmployeeRequestById(id));
    }

    @PatchMapping("/me/{id}/cancel")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "Cancel my work request", description = "Cancels one own PENDING work request.")
    public ResponseEntity<WorkRequestResponseDTO> cancelMyWorkRequest(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(workRequestService.cancelCurrentEmployeeRequest(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List work requests", description = "Returns paginated work requests filtered by employee, branch, department, date range, type, or status.")
    public ResponseEntity<PageResponseDTO<WorkRequestResponseDTO>> getWorkRequests(
            @ParameterObject WorkRequestFilterDTO filters,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(workRequestService.findAllByFilter(filters, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get work request", description = "Returns a work request by ID.")
    public ResponseEntity<WorkRequestResponseDTO> getWorkRequest(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(workRequestService.findById(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Approve work request", description = "Approves a PENDING work request, records reviewer data, and updates the work schedule.")
    public ResponseEntity<WorkRequestResponseDTO> approveWorkRequest(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody(required = false) WorkRequestReviewDTO review
    ) {
        return ResponseEntity.ok(workRequestService.approve(id, review));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Reject work request", description = "Rejects a PENDING work request and records reviewer data.")
    public ResponseEntity<WorkRequestResponseDTO> rejectWorkRequest(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody(required = false) WorkRequestReviewDTO review
    ) {
        return ResponseEntity.ok(workRequestService.reject(id, review));
    }
}
