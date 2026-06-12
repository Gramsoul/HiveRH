package com.HiveGroup.HiveRH.Features.Variation;

import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationFilterDTO;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationRequest;
import com.HiveGroup.HiveRH.Features.Variation.DTO.VariationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variations")
@RequiredArgsConstructor
public class VariationController {

    private final VariationService variationService;

    @GetMapping("/{id}")
    public ResponseEntity<VariationResponse> getVariationById(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<VariationResponse>> getVariations(VariationFilterDTO filters) {
        return ResponseEntity.ok(variationService.findAllByFilter(filters));
    }

    @PostMapping
    public ResponseEntity<VariationResponse> createVariation(
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(variationService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VariationResponse> patchVariation(
            @PathVariable Long id,
            @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.patchById(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariationResponse> putVariation(
            @PathVariable Long id,
            @Valid @RequestBody VariationRequest request
    ) {
        return ResponseEntity.ok(variationService.putById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VariationResponse> deleteVariation(@PathVariable Long id) {
        return ResponseEntity.ok(variationService.deleteById(id));
    }
}