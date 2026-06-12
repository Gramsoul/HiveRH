package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
@AllArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    public ResponseEntity<List<PayrollResponse>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollResponse> getPayrollById(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PayrollResponse> deletePayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.deleteById(id));
    }
}