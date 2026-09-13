package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.PageResponseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseReviewRequestDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/licenses")
@AllArgsConstructor
@Validated
@Tag(name = "13 Licenses", description = "Employee licenses and approval status.")
public class LicenseController {
    LicenseService licenseService;

    @GetMapping
    @Operation(summary = "List licenses", description = "Returns paginated licenses and supports filtering by status, employee DNI, date range, and paid status.")
    public ResponseEntity<PageResponseDTO<LicenseDTO>> getLicenses(
            @ParameterObject @Valid LicenseFilterDTO filters,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicensePage(filters, pageable)
        );
    }

    @GetMapping("/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    @Operation(summary = "Get license", description = "Returns a license by ID. Authorization validates access to the requested license.")
    public ResponseEntity<LicenseDTO> getLicenseByID(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping
    @PreAuthorize("@securityAuthorizationService.hasLinkedEmployee()")
    @Operation(summary = "Create license", description = "Creates a license associated with the employee linked to the authenticated account and can link existing certificates.")
    public ResponseEntity<LicenseDTO> postLicense(@Valid @RequestBody RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/{id_license}")
    @Operation(summary = "Review license", description = "Allows STAFF or ADMIN users to update the license status, paid flag, and review comment.")
    public ResponseEntity<LicenseDTO> reviewLicense(
            @PathVariable("id_license") @Positive(message = "El ID de la licencia debe ser mayor que cero") Long idLicense,
            @Valid @RequestBody LicenseReviewRequestDTO request) {
        return ResponseEntity.ok().body(licenseService.reviewLicense(idLicense, request));
    }

    @DeleteMapping("/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canDeleteLicense(#id_license)")
    @Operation(summary = "Delete license", description = "Deletes a license if the authenticated user has permission to access it.")
    public ResponseEntity<Void> deleteLicense(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
