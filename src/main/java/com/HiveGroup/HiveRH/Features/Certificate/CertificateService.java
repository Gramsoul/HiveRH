package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Common.Utils.DTOs.MessageDTO;
import com.HiveGroup.HiveRH.Common.Utils.Exception.FileProcessingException;
import com.HiveGroup.HiveRH.Common.Utils.Services.PdfLectorService;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CertificateService {
    CertificateRepository certificateRepository;
    CertificateMapper certificateMapper;
    PdfLectorService pdfLectorService;
    LicenseService licenseService;

    public CertificateEntity toEntity(CertificateDTO certificate) {
        return certificateMapper.toEntity(certificate);
    }

    public CertificateDTO toDTO(CertificateEntity certificate) {
        return certificateMapper.toDTO(certificate);
    }

    @Transactional
    public List<CertificateEntity> getCertificates(List<Long> ids) {
        List<CertificateEntity> certificates = new ArrayList<>();

        for (Long id : ids) {
            CertificateEntity cer =
                    certificateRepository.findById(id).orElseThrow(() -> new CertificateNotFoundException(""));
            certificates.add(cer);
        }

        return certificates;
    }

    public List<Long> getCertificateID(List<CertificateEntity> certificateEntityList) {

        List<Long> idCert = new ArrayList<>();

        for (CertificateEntity c : certificateEntityList) {
            idCert.add(c.getId_certificate());
        }

        return idCert;
    }

    @Transactional
    public CertificateDTO createCertificate(Long idLicense, String description, MultipartFile file) {
        try {
            byte[] pdf = pdfLectorService.savePDF(file);
            LicenseEntity license = licenseService.getEntity(licenseService.getLicense(idLicense));

            CertificateEntity certificate = CertificateEntity.builder()
                    .description(description)
                    .license(license)
                    .file(pdf).build();

            license.getCertificates().add(certificate);
            certificateRepository.save(certificate);

            return toDTO(certificate);
        } catch (IOException e){
            throw new FileProcessingException("");
        }
    }

    public void deleteCertificate(Long id){
        CertificateEntity c = certificateRepository.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(""));
        certificateRepository.delete(c);
    }

    public ResponseCertificateDTO getInfoCertificate(Long id){
        CertificateEntity c = certificateRepository.findById(id).orElseThrow(() -> new CertificateNotFoundException(""));
        return ResponseCertificateDTO.builder()
                .description(c.getDescription())
                .idLicense(c.getLicense().getId_license())
                .build();
    }

    public byte[] loadPDF(Long id){
        CertificateEntity c = certificateRepository.findById(id)
                .orElseThrow(() -> new CertificateNotFoundException(""));
        return  c.getFile();
    }



}
