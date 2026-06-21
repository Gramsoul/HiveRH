package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
@Validated
@Tag(name = "Certificates", description = "Carga, consulta y descarga de certificados PDF.")
public class CertificateController {
    CertificateService certificateService;

    @PostMapping("/api/certificate")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#idLicense)")
    @Operation(summary = "Cargar certificado PDF", description = "Carga un archivo PDF mediante multipart/form-data y lo asocia a una licencia.")
    public ResponseEntity<CertificateDTO> createCertificate(
            @P("idLicense") @RequestParam("idLicense") @NotNull @Positive(message = "El ID de la licencia debe ser mayor que cero") Long idLicense,
            @RequestParam("description") @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres") String description,
            @RequestParam("file") @NotNull(message = "El archivo del certificado es obligatorio") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificateService.createCertificate(idLicense, description, file));
    }

    @GetMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    @Operation(summary = "Descargar certificado PDF", description = "Devuelve el archivo PDF almacenado para el certificado indicado.")
    public ResponseEntity<byte[]> loadPDF(
            @P("id_certificate") @PathVariable @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id_certificate) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(certificateService.loadPDF(id_certificate));
    }

    @GetMapping("/api/certificate-info")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id)")
    @Operation(summary = "Consultar informacion de certificado", description = "Devuelve metadatos del certificado sin descargar el archivo PDF.")
    public ResponseEntity<ResponseCertificateDTO> getInfo(
            @P("id") @RequestParam @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id) {
        return ResponseEntity.ok().body(certificateService.getInfoCertificate(id));
    }

    @DeleteMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    @Operation(summary = "Eliminar certificado", description = "Elimina el certificado indicado si el usuario tiene permisos sobre el recurso.")
    public ResponseEntity<Void> deleteCertificate(
            @P("id_certificate") @PathVariable @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id_certificate) {
        certificateService.deleteCertificate(id_certificate);
        return ResponseEntity.noContent().build();
    }
}
