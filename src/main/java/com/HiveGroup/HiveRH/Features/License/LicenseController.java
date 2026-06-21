package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class LicenseController {
    LicenseService licenseService;

    @GetMapping("/api/license")
    public ResponseEntity<List<LicenseDTO>> getLicenses(@Valid LicenseFilterDTO filters) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO(filters)
        );
    }

    @GetMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    public ResponseEntity<LicenseDTO> getLicenseByID(@P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping("/api/license")
    @PreAuthorize("@securityAuthorizationService.canCreateLicenseForEmployee(#license.idEmployee())")
    public ResponseEntity<LicenseDTO> postLicense(@P("license") @Valid @RequestBody RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/api/license")
    public ResponseEntity<LicenseDTO> patchLicense(@Valid @RequestBody LicenseDTO license) {
        return ResponseEntity.ok().body(licenseService.patchLicense(license));
    }

    @DeleteMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canDeleteLicense(#id_license)")
    public ResponseEntity<Void> deleteLicense(@P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
