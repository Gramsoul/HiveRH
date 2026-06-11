package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacation")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    // GET - api/vacation
    @GetMapping
    public ResponseEntity<List<VacationResponse>> findAll() {

        List<VacationResponse> response = vacationService.findAll();

        return ResponseEntity.ok(response);
    }

    // GET - api/vacation/{id_vacation}
    @GetMapping("/{id_vacation}")
    public ResponseEntity<VacationResponse> findById(@PathVariable("id_vacation") Long idVacation) {

        VacationResponse response = vacationService.findById(idVacation);

        return ResponseEntity.ok(response);
    }

    // POST - api/vacation
    @PostMapping
    public ResponseEntity<VacationResponse> create(@Valid @RequestBody VacationRequest request) {

        VacationResponse response = vacationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT - api/vacation/{id_vacation}
    @PutMapping("/{id_vacation}")
    public ResponseEntity<VacationResponse> updateById(
            @PathVariable("id_vacation") Long idVacation,
            @Valid @RequestBody VacationRequest request
    ) {

        VacationResponse response = vacationService.updateById(idVacation, request);

        return ResponseEntity.ok(response);
    }

    // DELETE - api/vacation/{id_vacation}
    @DeleteMapping("/{id_vacation}")
    public ResponseEntity<VacationResponse> deleteById(@PathVariable("id_vacation") Long idVacation) {

        VacationResponse response = vacationService.deleteById(idVacation);

        return ResponseEntity.ok(response);
    }
}