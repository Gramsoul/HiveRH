package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Licenses", description = "Licencias de empleados y su estado de aprobacion.")
public class LicenseController {
    LicenseService licenseService;

    @GetMapping("/api/license")
    @Operation(summary = "Listar licencias", description = "Lista licencias y permite aplicar filtros disponibles.")
    public ResponseEntity<List<LicenseDTO>> getLicenses(LicenseFilterDTO filters) {
        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO(filters)
        );
    }

    @GetMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#id_license)")
    @Operation(summary = "Consultar licencia", description = "Obtiene una licencia por ID. La autorizacion valida acceso a la licencia solicitada.")
    public ResponseEntity<LicenseDTO> getLicenseByID(@P("id_license") @PathVariable @NotNull Long id_license) {
        return ResponseEntity.ok().body(licenseService.getLicense(id_license));
    }

    @PostMapping("/api/license")
    @PreAuthorize("@securityAuthorizationService.canCreateLicenseForEmployee(#license.idEmployee())")
    @Operation(summary = "Crear licencia", description = "Crea una licencia asociada a un empleado y puede vincular certificados existentes.")
    public ResponseEntity<LicenseDTO> postLicense(@P("license") @RequestBody @NotNull RequestLicenseDTO license) {
        return ResponseEntity.status(HttpStatus.CREATED).body(licenseService.createLicense(license));
    }

    @PatchMapping("/api/license")
    @Operation(summary = "Actualizar licencia parcialmente", description = "Modifica solo los campos enviados de una licencia.")
    public ResponseEntity<LicenseDTO> patchLicense(@RequestBody @NotNull LicenseDTO license) {
        return ResponseEntity.ok().body(licenseService.patchLicense(license));
    }

    @DeleteMapping("/api/license/{id_license}")
    @PreAuthorize("@securityAuthorizationService.canDeleteLicense(#id_license)")
    @Operation(summary = "Eliminar licencia", description = "Elimina una licencia si el usuario autenticado tiene permisos sobre ella.")
    public ResponseEntity<Void> deleteLicense(@P("id_license") @PathVariable @NotNull Long id_license) {
        licenseService.deleteLicense(id_license);
        return ResponseEntity.noContent().build();
    }
}
