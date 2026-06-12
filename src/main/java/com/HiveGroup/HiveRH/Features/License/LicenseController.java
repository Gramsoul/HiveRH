package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class LicenseController {
    LicenseService licenseService;

    @GetMapping("/api/license")
    public ResponseEntity<List<LicenseDTO>> getLicenses(LicenseFilterDTO filters) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO(filters)
        );
    }

    @GetMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    public ResponseEntity<LicenseDTO> getLicenseByID(@P("id_license") @PathVariable @NotNull Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping("/api/license")
    @PreAuthorize("@securityAuthorizationService.canCreateLicenseForEmployee(#license.idEmployee())")
    public ResponseEntity<LicenseDTO> postLicense(@P("license") @RequestBody @NotNull RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/api/license")
    public ResponseEntity<LicenseDTO> patchLicense(@RequestBody @NotNull LicenseDTO license) {
        return ResponseEntity.ok().body(licenseService.patchLicense(license));
    }

    @DeleteMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    public ResponseEntity<Void> deleteLicense(@P("id_license") @PathVariable @NotNull Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
