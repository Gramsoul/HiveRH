package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationFilterDTO;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variations")
@RequiredArgsConstructor
@Tag(name = "Variations", description = "Conceptos salariales que suman o descuentan en liquidaciones.")
public class VariationController {

    private final VariationService variationService;

    @GetMapping("/{id}")
    @Operation(summary = "Consultar variacion", description = "Obtiene una variacion salarial por ID.")
    public ResponseEntity<VariationResponse> getVariationById(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar variaciones", description = "Lista variaciones salariales y permite aplicar filtros disponibles.")
    public ResponseEntity<List<VariationResponse>> getVariations(VariationFilterDTO filters) {
        return ResponseEntity.ok(variationService.findAllByFilter(filters));
    }

    @PostMapping
    @Operation(summary = "Crear variacion", description = "Crea un concepto salarial. Un total positivo suma al sueldo y un total negativo descuenta.")
    public ResponseEntity<VariationResponse> createVariation(
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(variationService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar variacion parcialmente", description = "Modifica solo los campos enviados de una variacion salarial.")
    public ResponseEntity<VariationResponse> patchVariation(
            @PathVariable Long id,
            @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.patchById(id, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar variacion", description = "Actualiza los datos principales de una variacion salarial existente.")
    public ResponseEntity<VariationResponse> putVariation(
            @PathVariable Long id,
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.putById(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar variacion", description = "Elimina una variacion salarial.")
    public ResponseEntity<VariationResponse> deleteVariation(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.deleteById(id));
    }
}
