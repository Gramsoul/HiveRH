package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Common.Utils.Services.PdfLectorService;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@Controller
public class CertificateController {

    PdfLectorService pdfLectorService;
    CertificateRepository certificateRepository;
    LicenseRepository licenseRepository;
    CertificateService certificateService;

    public CertificateController(
            PdfLectorService pdfLectorService,
            CertificateRepository certificateRepository,
            LicenseRepository licenseRepository,
            CertificateService certificateService
    ) {
        this.pdfLectorService = pdfLectorService;
        this.certificateRepository = certificateRepository;
        this.licenseRepository = licenseRepository;
        this.certificateService = certificateService;
    }

    @PostMapping("/api/saveCertificate")
    public ResponseEntity<LicenseEntity> savePDF(@RequestBody @NotNull MultipartFile file) throws IOException {
        byte[] pdf = pdfLectorService.savePDF(file);
        LicenseEntity license = licenseRepository.findById((long) 1).get();
        CertificateEntity certificate = certificateService.createCertificate(pdf, license, "test");
        license.getCertificates().add(certificate);

        certificateRepository.save(certificate);
        licenseRepository.save(license);
        return ResponseEntity.ok().body(license);
    }

    @GetMapping("api/loadCertificate")
    public void loadPDF(@RequestParam long idCertificate) throws IOException {
        CertificateEntity certificate = certificateRepository.findById(idCertificate).get();
        pdfLectorService.loadPDF(certificate.getFile());
    }
}
