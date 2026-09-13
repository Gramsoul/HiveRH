package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollPatchRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
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
@RequestMapping("/api/payrolls")
@AllArgsConstructor
@Tag(name = "11 Payrolls", description = "Monthly payroll records, details, and employee payroll history.")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping("/me")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "List my payrolls", description = "Returns confirmed payrolls for the employee linked to the authenticated account. Supports optional year filter.")
    public ResponseEntity<List<PayrollResponse>> getMyPayrolls(@RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(payrollService.findCurrentEmployeePayrolls(year));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "Get my payroll", description = "Returns a confirmed payroll of the employee linked to the authenticated account.")
    public ResponseEntity<PayrollResponse> getMyPayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.findCurrentEmployeePayrollById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List payrolls", description = "Returns paginated payroll records filtered by period, month, year, status, or employee DNI.")
    public ResponseEntity<PageResponseDTO<PayrollResponse>> getPayrolls(
            @ParameterObject PayrollFilterDTO filters,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(payrollService.findAllByFilter(filters, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get payroll", description = "Returns a payroll with its details.")
    public ResponseEntity<PayrollResponse> getPayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create payroll draft", description = "Creates a DRAFT payroll for an employee and payroll period using base salary snapshot plus detail concepts.")
    public ResponseEntity<PayrollResponse> createPayroll(@Valid @RequestBody PayrollRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Update payroll draft", description = "Updates a DRAFT payroll and recalculates additions, deductions, and total.")
    public ResponseEntity<PayrollResponse> updatePayroll(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody PayrollPatchRequest request
    ) {
        return ResponseEntity.ok(payrollService.updateById(id, request));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Confirm payroll", description = "Confirms a DRAFT payroll.")
    public ResponseEntity<PayrollResponse> confirmPayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.confirm(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Cancel payroll", description = "Cancels a payroll while its period is still open.")
    public ResponseEntity<PayrollResponse> cancelPayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.cancel(id));
    }
}
