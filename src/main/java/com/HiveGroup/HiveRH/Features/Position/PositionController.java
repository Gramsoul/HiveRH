package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Features.Position.DTO.PositionFilterDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionRequestDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionResponseDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionStatusRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@AllArgsConstructor
@Validated
@Tag(name = "05 Positions", description = "Job position management.")
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    @Operation(summary = "List positions", description = "Returns job positions and supports filtering by department, name, and active status.")
    public ResponseEntity<List<PositionResponseDTO>> getPositions(@ParameterObject @Valid PositionFilterDTO filters) {
        return ResponseEntity.ok(positionService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Create position", description = "Registers a new job position.")
    public ResponseEntity<PositionResponseDTO> createPosition(@Valid @RequestBody PositionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update position", description = "Updates the main data of an existing job position.")
    public ResponseEntity<PositionResponseDTO> updatePosition(
            @NonNull @PathVariable @Positive(message = "El ID del puesto debe ser mayor que cero") Long id,
            @Valid @RequestBody PositionRequestDTO request) {
        return ResponseEntity.ok(positionService.updateById(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update position status", description = "Activates or deactivates a job position using logical status changes.")
    public ResponseEntity<PositionResponseDTO> updatePositionStatus(
            @NonNull @PathVariable @Positive(message = "El ID del puesto debe ser mayor que cero") Long id,
            @Valid @RequestBody PositionStatusRequestDTO request) {
        return ResponseEntity.ok(positionService.updateStatus(id, request.active()));
    }
}
