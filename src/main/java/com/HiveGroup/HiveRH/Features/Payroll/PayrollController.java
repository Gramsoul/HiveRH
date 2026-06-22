package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollFilterDTO;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollRequest;
import com.HiveGroup.HiveRH.Features.Payroll.DTO.PayrollResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Payrolls", description = "Liquidaciones de sueldo y consultas por empleado.")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Listar liquidaciones", description = "Lista liquidaciones de sueldo. Requiere rol ADMIN o RRHH.")
    public ResponseEntity<PageResponseDTO<PayrollResponse>> getAllPayrollsForPage(Pageable pageable) {
        return ResponseEntity.ok(payrollService.getAllPages(pageable));
    }

    @GetMapping("/employee/{id_employee}")
    @PreAuthorize("@securityAuthorizationService.canAccessEmployee(#idEmployee)")
    @Operation(summary = "Listar liquidaciones por empleado", description = "Consulta liquidaciones de un empleado con filtros de fecha. Un empleado solo puede acceder a sus propias liquidaciones.")
    public ResponseEntity<List<PayrollResponse>> getPayrollsByEmployee(
            @P("idEmployee") @PathVariable("id_employee") String dniEmployee,
            PayrollFilterDTO filters
    ) {
        return ResponseEntity.ok(payrollService.findAllByEmployee(dniEmployee, filters));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Crear liquidacion", description = "Genera una liquidacion calculando sueldo base mas variaciones. Valida empleado activo, sueldo valido y una unica liquidacion por mes.")
    public ResponseEntity<PayrollResponse> createPayroll(
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payrollService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Actualizar liquidacion", description = "Actualiza una liquidacion existente recalculando datos segun el request enviado.")
    public ResponseEntity<PayrollResponse> updatePayroll(
            @NonNull @PathVariable Long id,
            @Valid @RequestBody PayrollRequest request
    ) {
        return ResponseEntity.ok(payrollService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    @Operation(summary = "Eliminar liquidacion", description = "Elimina una liquidacion y devuelve los datos eliminados.")
    public ResponseEntity<PayrollResponse> deletePayroll(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(payrollService.deleteById(id));
    }
}
