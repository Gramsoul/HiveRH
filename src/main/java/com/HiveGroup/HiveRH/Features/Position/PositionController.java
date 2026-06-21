package com.HiveGroup.HiveRH.Features.Position;

import com.HiveGroup.HiveRH.Features.Position.DTO.PositionFilterDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionRequestDTO;
import com.HiveGroup.HiveRH.Features.Position.DTO.PositionResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/position")
@AllArgsConstructor
@Validated
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    public ResponseEntity<List<PositionResponseDTO>> getPositions(@Valid PositionFilterDTO filters) {
        return ResponseEntity.ok(positionService.findAllByFilter(filters));
    }

    @PostMapping
    public ResponseEntity<PositionResponseDTO> createPosition(@Valid @RequestBody PositionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PositionResponseDTO> deletePosition(@NonNull @PathVariable @Positive(message = "El ID del puesto debe ser mayor que cero") Long id) {
        return ResponseEntity.ok(positionService.deleteById(id));
    }
}
