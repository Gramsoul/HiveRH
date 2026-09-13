package com.HiveGroup.HiveRH.Features.PayrollConcept;

import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptFilterDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptPatchDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptRequestDTO;
import com.HiveGroup.HiveRH.Features.PayrollConcept.DTO.PayrollConceptResponseDTO;
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
@RequestMapping("/api/payroll-concepts")
@AllArgsConstructor
@Tag(name = "10 Payroll Concepts", description = "Reusable payroll concepts for additions and deductions.")
public class PayrollConceptController {

    private final PayrollConceptService payrollConceptService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "List payroll concepts", description = "Returns payroll concepts filtered by name, type, or active status.")
    public ResponseEntity<List<PayrollConceptResponseDTO>> getPayrollConcepts(@ParameterObject PayrollConceptFilterDTO filters) {
        return ResponseEntity.ok(payrollConceptService.findAll(filters));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get payroll concept", description = "Returns a payroll concept by ID.")
    public ResponseEntity<PayrollConceptResponseDTO> getPayrollConcept(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollConceptService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Create payroll concept", description = "Creates a reusable payroll concept classified as ADDITION or DEDUCTION.")
    public ResponseEntity<PayrollConceptResponseDTO> createPayrollConcept(@Valid @RequestBody PayrollConceptRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollConceptService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Partially update payroll concept", description = "Updates only the provided fields of a payroll concept.")
    public ResponseEntity<PayrollConceptResponseDTO> patchPayrollConcept(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody PayrollConceptPatchDTO request
    ) {
        return ResponseEntity.ok(payrollConceptService.patchById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Deactivate payroll concept", description = "Marks a payroll concept as inactive without deleting historical payroll details.")
    public ResponseEntity<PayrollConceptResponseDTO> deletePayrollConcept(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollConceptService.deactivate(id));
    }
}
