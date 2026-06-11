package com.HiveGroup.HiveRH.Features.Suspension;

import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionFilterDTO;
import com.HiveGroup.HiveRH.Features.Suspension.DTO.SuspensionResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspension")
@AllArgsConstructor
public class SuspensionController {
    private final SuspensionService suspensionService;

    @GetMapping
    public ResponseEntity<List<SuspensionResponseDTO>> getSuspensions(SuspensionFilterDTO filters) {
        return ResponseEntity.ok(suspensionService.findAllByFilter(filters));
    }
}
