package com.HiveGroup.HiveRH.Features.PayrollPeriod;

import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodCreateDTO;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodFilterDTO;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.DTO.PayrollPeriodResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-periods")
@AllArgsConstructor
@Tag(name = "09 Payroll Periods", description = "Monthly payroll periods and closing workflow.")
public class PayrollPeriodController {

    private final PayrollPeriodService payrollPeriodService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List payroll periods", description = "Returns payroll periods filtered by month, year, or status.")
    public ResponseEntity<List<PayrollPeriodResponseDTO>> getPayrollPeriods(@ParameterObject PayrollPeriodFilterDTO filters) {
        return ResponseEntity.ok(payrollPeriodService.findAll(filters));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get payroll period", description = "Returns a payroll period by ID.")
    public ResponseEntity<PayrollPeriodResponseDTO> getPayrollPeriod(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollPeriodService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create payroll period", description = "Creates an OPEN payroll period for a month and year.")
    public ResponseEntity<PayrollPeriodResponseDTO> createPayrollPeriod(@Valid @RequestBody PayrollPeriodCreateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollPeriodService.create(request));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Close payroll period", description = "Closes an OPEN payroll period when it has no DRAFT payrolls.")
    public ResponseEntity<PayrollPeriodResponseDTO> closePayrollPeriod(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollPeriodService.close(id));
    }
}
