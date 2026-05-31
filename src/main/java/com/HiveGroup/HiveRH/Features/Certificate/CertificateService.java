package com.HiveGroup.HiveRH.Features.Certificate;


import com.HiveGroup.HiveRH.Common.Utils.Exception.FileProcessingException;
import com.HiveGroup.HiveRH.Common.Utils.Services.PdfLectorService;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.HiveGroup.HiveRH.Features.License.LicenseNotFoundException;
import com.HiveGroup.HiveRH.Features.License.LicenseRepository;
import com.HiveGroup.HiveRH.Features.License.LicenseService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CertificateService {
    CertificateRepository certificateRepository;
    PdfLectorService pdfLectorService;
    LicenseRepository licenseRepository;
    @Autowired
    CertificateMapper certificateMapper;

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
            LicenseEntity license = licenseRepository.findById(idLicense)
                    .orElseThrow(() -> new LicenseNotFoundException(""));

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
