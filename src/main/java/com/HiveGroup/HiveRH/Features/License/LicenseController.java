package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@Tag(name = "Licenses", description = "Licencias de empleados y su estado de aprobacion.")
public class LicenseController {
    LicenseService licenseService;

    @GetMapping("/api/license")
    @Operation(summary = "Listar licencias", description = "Lista licencias y permite aplicar filtros disponibles.")
    public ResponseEntity<List<LicenseDTO>> getLicenses(@Valid LicenseFilterDTO filters) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO(filters)
        );
    }

    @GetMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    @Operation(summary = "Consultar licencia", description = "Obtiene una licencia por ID. La autorizacion valida acceso a la licencia solicitada.")
    public ResponseEntity<LicenseDTO> getLicenseByID(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping("/api/license")
    @PreAuthorize("@securityAuthorizationService.canCreateLicenseForEmployee(#license.idEmployee())")
    @Operation(summary = "Crear licencia", description = "Crea una licencia asociada a un empleado y puede vincular certificados existentes.")
    public ResponseEntity<LicenseDTO> postLicense(@P("license") @Valid @RequestBody RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/api/license")
    @Operation(summary = "Actualizar licencia parcialmente", description = "Modifica solo los campos enviados de una licencia.")
    public ResponseEntity<LicenseDTO> patchLicense(@Valid @RequestBody LicenseDTO license) {
        return ResponseEntity.ok().body(licenseService.patchLicense(license));
    }

    @DeleteMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canDeleteLicense(#id_license)")
    @Operation(summary = "Eliminar licencia", description = "Elimina una licencia si el usuario autenticado tiene permisos sobre ella.")
    public ResponseEntity<Void> deleteLicense(
            @P("id_license") @PathVariable @Positive(message = "El ID de la licencia debe ser mayor que cero") Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
