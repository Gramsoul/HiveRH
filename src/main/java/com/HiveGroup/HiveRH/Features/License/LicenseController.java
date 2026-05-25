package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class LicenseController {
    LicenseService licenseService;

    @GetMapping("/api/license")
    public ResponseEntity<List<LicenseDTO>> getLicenses() {
        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO()
        );
    }

    @GetMapping("/api/license/{id_license}")
    public ResponseEntity<LicenseDTO> getLicenseByID(@PathVariable @NotNull Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping("/api/license")
    public ResponseEntity<LicenseDTO> postLicense(@RequestBody @NotNull LicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));

    }

    @PatchMapping("/api/license")
    public ResponseEntity<LicenseDTO> patchLicense(@RequestBody @NotNull LicenseDTO license) {
        return ResponseEntity.ok().body(licenseService.patchLicense(license));
    }
}
