package com.HiveGroup.HiveRH.Features.Vacation;

import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationFilterDTO;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationRequest;
import com.HiveGroup.HiveRH.Features.Vacation.DTO.VacationResponse;
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
public class VacationController {

    private final VacationService vacationService;

    @GetMapping
    public ResponseEntity<List<VacationResponse>> findAll(VacationFilterDTO filters) {

        List<VacationResponse> response = vacationService.findAllByFilter(filters);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.canCreateVacationForEmployee(#request.idEmployee())")
    public ResponseEntity<VacationResponse> create(@P("request") @Valid @RequestBody VacationRequest request) {

        VacationResponse response = vacationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id_vacation}")
    public ResponseEntity<VacationResponse> updateById(
            @PathVariable("id_vacation") Long idVacation,
            @Valid @RequestBody VacationRequest request
    ) {

        VacationResponse response = vacationService.updateById(idVacation, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id_vacation}")
    @PreAuthorize("@securityAuthorizationService.canDeleteVacation(#idVacation)")
    public ResponseEntity<VacationResponse> deleteById(
            @P("idVacation") @PathVariable("id_vacation") Long idVacation
    ) {

        VacationResponse response = vacationService.deleteById(idVacation);

        return ResponseEntity.ok(response);
    }
}
