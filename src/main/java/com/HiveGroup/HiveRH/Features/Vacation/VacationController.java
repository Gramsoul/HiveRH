package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationFilterDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
@Tag(name = "Vacations", description = "Solicitudes y registros de vacaciones.")
public class VacationController {

    private final VacationService vacationService;

    @GetMapping
    @Operation(summary = "Listar vacaciones", description = "Lista vacaciones y permite filtrar por estado de aceptacion, rango de fechas y nombre completo.")
    public ResponseEntity<List<VacationResponse>> findAll(VacationFilterDTO filters) {

        List<VacationResponse> response = vacationService.findAllByFilter(filters);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.canCreateVacationForEmployee(#request.idEmployee())")
    @Operation(summary = "Registrar vacaciones", description = "Registra vacaciones para un empleado activo. Valida fechas y evita superposiciones para el mismo empleado.")
    public ResponseEntity<VacationResponse> create(@P("request") @Valid @RequestBody VacationRequest request) {

        VacationResponse response = vacationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id_vacation}")
    @Operation(summary = "Actualizar vacaciones", description = "Actualiza un registro de vacaciones existente.")
    public ResponseEntity<VacationResponse> updateById(
            @PathVariable("id_vacation") Long idVacation,
            @Valid @RequestBody VacationRequest request
    ) {

        VacationResponse response = vacationService.updateById(idVacation, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id_vacation}")
    @PreAuthorize("@securityAuthorizationService.canDeleteVacation(#idVacation)")
    @Operation(summary = "Eliminar vacaciones", description = "Elimina el registro indicado. La autorizacion valida si el usuario puede eliminar esa solicitud.")
    public ResponseEntity<VacationResponse> deleteById(
            @P("idVacation") @PathVariable("id_vacation") Long idVacation
    ) {

        VacationResponse response = vacationService.deleteById(idVacation);

        return ResponseEntity.ok(response);
    }
}
