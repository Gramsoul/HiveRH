package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Features.Position.DTO.PositionFilterDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionRequestDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@AllArgsConstructor
@Tag(name = "Positions", description = "Administracion de puestos de trabajo.")
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    @Operation(summary = "Listar puestos", description = "Obtiene puestos de trabajo y permite filtrar por departamento, nombre y estado activo.")
    public ResponseEntity<List<PositionResponseDTO>> getPositions(PositionFilterDTO filters) {
        return ResponseEntity.ok(positionService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear puesto", description = "Registra un nuevo puesto de trabajo.")
    public ResponseEntity<PositionResponseDTO> createPosition(@NonNull @RequestBody PositionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja puesto", description = "Realiza una baja logica del puesto, dejandolo inactivo.")
    public ResponseEntity<PositionResponseDTO> deletePosition(@NonNull @PathVariable Long id) {
        return ResponseEntity.ok(positionService.deleteById(id));
    }
}
