package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@AllArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

//    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
//    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
//        return ResponseEntity.ok(payrollService.findAll());
//    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<PageResponseDTO<PayrollResponse>> getAllPayrollsForPage(Pageable pageable) {
        return ResponseEntity.ok(payrollService.getAllPages(pageable));
    }

    @GetMapping("/employee/{id_employee}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployee(#idEmployee)")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
            @P("idEmployee") @PathVariable("id_employee") Long idEmployee,
            PayrollFilterDTO filters
    ) {
        return ResponseEntity.ok(payrollService.findAllByEmployee(idEmployee, filters));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<PayrollResponse> updatePayroll(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity.ok(payrollService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<PayrollResponse> deletePayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.deleteById(id));
    }
}
