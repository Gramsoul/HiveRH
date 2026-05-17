package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Common.Utils.Services.PdfLectorService;
import com.HiveGroup.HiveRH.Features.License.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import com.HiveGroup.HiveRH.Features.License.LicenseService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Controller
public class CertificateController {

    PdfLectorService pdfLectorService;
    CertificateService certificateService;
    LicenseService licenseService;
    CertificateRepository certificateRepository;
    LicenseRepository licenseRepository;

    public CertificateController(
            PdfLectorService pdfLectorService,
            CertificateService certificateService,
            LicenseService licenseService,
            CertificateRepository certificateRepository,
            LicenseRepository licenseRepository
    ) {
        this.pdfLectorService = pdfLectorService;
        this.certificateService = certificateService;
        this.licenseService = licenseService;
        this.certificateRepository = certificateRepository;
        this.licenseRepository = licenseRepository;
    }

    @PostMapping("/api/saveCertificate")
    public ResponseEntity<CertificateDTO> savePDF(@RequestParam("idLicense") @NotNull Long idLicense,
                                                  @RequestParam("file") @NotNull MultipartFile file)
            throws IOException {

        byte[] pdf = pdfLectorService.savePDF(file);
        LicenseEntity license = licenseRepository.findById(idLicense).orElse(null);

        if (license == null) {
            return ResponseEntity.notFound().build();
        } else {
            CertificateEntity certificate = CertificateEntity.builder()
                    .description("Test description")
                    .license(license)
                    .file(pdf).build();

            license.getCertificates().add(certificate);
            certificateRepository.save(certificate); //se agrego porque no esta en cascada aun
            licenseRepository.save(license);

            return ResponseEntity.ok().body(
                    CertificateDTO.builder()
                            .description(certificate.getDescription())
                            .file(certificate.getFile())
                            .idLicense(license.getId_license())
                            .idCertificate(certificate.getId_certificate())
                            .build()
            );
        }
    }

    @GetMapping("api/loadCertificate")
    public ResponseEntity<byte[]> loadPDF(@RequestParam @NotNull long idCertificate){
        CertificateEntity certificate = certificateRepository.findById(idCertificate).orElse(null);
        if (certificate == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "inline; filename=certificate.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(certificate.getFile());
        }
    }
}
