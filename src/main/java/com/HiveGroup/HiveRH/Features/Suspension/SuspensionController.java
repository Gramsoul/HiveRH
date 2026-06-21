package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionRequestDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suspension")
@AllArgsConstructor
@Tag(name = "Suspensions", description = "Suspensiones de empleados y cambio de estado asociado.")
public class SuspensionController {
    private final SuspensionService suspensionService;

    @GetMapping
    @Operation(summary = "Listar suspensiones", description = "Lista suspensiones y permite aplicar filtros disponibles.")
    public ResponseEntity<List<SuspensionResponseDTO>> getSuspensions(@Valid SuspensionFilterDTO filters) {
        return ResponseEntity.ok(suspensionService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Registrar suspension", description = "Registra una suspension para un empleado y cambia automaticamente su estado a SUSPENDED.")
    public ResponseEntity<SuspensionResponseDTO> createSuspension(@Valid @RequestBody SuspensionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(suspensionService.create(request));
    }
}
