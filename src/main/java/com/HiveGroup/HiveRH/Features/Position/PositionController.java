package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Features.Position.DTO.PositionFilterDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionRequestDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@AllArgsConstructor
@Validated
@Tag(name = "Positions", description = "Administracion de puestos de trabajo.")
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    @Operation(summary = "Listar puestos", description = "Obtiene puestos de trabajo y permite filtrar por departamento, nombre y estado activo.")
    public ResponseEntity<List<PositionResponseDTO>> getPositions(@Valid PositionFilterDTO filters) {
        return ResponseEntity.ok(positionService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear puesto", description = "Registra un nuevo puesto de trabajo.")
    public ResponseEntity<PositionResponseDTO> createPosition(@Valid @RequestBody PositionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja puesto", description = "Realiza una baja logica del puesto, dejandolo inactivo.")
    public ResponseEntity<PositionResponseDTO> deletePosition(
            @NonNull @PathVariable @Positive(message = "El ID del puesto debe ser mayor que cero") Long id) {
        return ResponseEntity.ok(positionService.deleteById(id));
    }
}
