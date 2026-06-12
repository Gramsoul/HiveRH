package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.MessageDTO;
import com.HiveGroup.HiveRH.Common.Utils.Services.PdfLectorService;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import com.HiveGroup.HiveRH.Features.License.LicenseService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@AllArgsConstructor
public class CertificateController {
    CertificateService certificateService;


    @PostMapping("/api/certificate")
    @PreAuthorize("@securityAuthorizationService.canAccessLicense(#idLicense)")
    public ResponseEntity<CertificateDTO> createCertificate(
            @P("idLicense") @RequestParam("idLicense") @NotNull Long idLicense,
            @RequestParam("description") String description,
            @RequestParam("file") @NotNull MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificateService.createCertificate(idLicense, description, file));
    }

    @GetMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    public ResponseEntity<byte[]> loadPDF(@P("id_certificate") @PathVariable @NotNull Long id_certificate) {
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "inline; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(certificateService.loadPDF(id_certificate));
    }

    @GetMapping("/api/certificate-info")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id)")
    public ResponseEntity<ResponseCertificateDTO> getInfo(@P("id") @RequestParam @NotNull Long id) {
        return ResponseEntity.ok().body(certificateService.getInfoCertificate(id));
    }

    @DeleteMapping("/api/certificate/{id_certificate}")
    @PreAuthorize("@securityAuthorizationService.canAccessCertificate(#id_certificate)")
    public ResponseEntity<Void> deleteCertificate(@P("id_certificate") @PathVariable @NotNull Long id_certificate) {
        certificateService.deleteCertificate(id_certificate);
        return ResponseEntity.noContent().build();
    }





}
