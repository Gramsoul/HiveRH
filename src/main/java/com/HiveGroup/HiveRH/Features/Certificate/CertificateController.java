package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Common.Utils.Services.FileLectorService;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.RequestCertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
@Validated
@Tag(name = "14 Certificates", description = "PDF certificate upload, lookup, and download.")
public class CertificateController {
    CertificateService certificateService;
    FileLectorService fileLectorService;

    @PostMapping(value = "/api/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#request.idLicense())")
    @Operation(summary = "Upload PDF certificate", description = "Uploads a PDF file through multipart/form-data and links it to a license.")
    public ResponseEntity<CertificateDTO> createCertificate(
            @P("request") @Valid @ModelAttribute RequestCertificateDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificateService.createCertificate(request.idLicense(), request.description(), request.file()));
    }

    @GetMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    @Operation(summary = "Download PDF certificate", description = "Returns the stored PDF file for the selected certificate.")
    public ResponseEntity<byte[]> loadPDF(
            @P("id_certificate") @PathVariable @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id_certificate) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(certificateService.loadPDF(id_certificate));
    }

    @GetMapping("/api/certificate-info")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id)")
    @Operation(summary = "Get certificate info", description = "Returns certificate metadata without downloading the PDF file.")
    public ResponseEntity<ResponseCertificateDTO> getInfo(
            @P("id") @RequestParam @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id) {
        return ResponseEntity.ok().body(certificateService.getInfoCertificate(id));
    }

    @DeleteMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    @Operation(summary = "Delete certificate", description = "Deletes the selected certificate when the user has permission over the resource.")
    public ResponseEntity<Void> deleteCertificate(
            @P("id_certificate") @PathVariable @NotNull(message = "El ID del certificado es obligatorio") @Positive(message = "El ID del certificado debe ser mayor que cero") Long id_certificate) {
        certificateService.deleteCertificate(id_certificate);
        return ResponseEntity.noContent().build();
    }


}
