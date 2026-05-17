package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateService {
    CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public CertificateEntity createCertificate(
            byte[] pdf,
            LicenseEntity license,
            String description
    ) {
        return CertificateEntity.builder()
                .file(pdf)
                .license(license)
                .description(description)
                .build();
    }

    public List<CertificateEntity> getCertificates(List<Long> ids) throws CertificateNotFoundException {
        List<CertificateEntity> certificates = new ArrayList<>();

        for (Long id : ids) {

            CertificateEntity cer =
                    certificateRepository.findById(id).orElse(null);

            if (cer == null) throw new CertificateNotFoundException("Certificate not found" + id);
            else certificates.add(cer);
        }

        return certificates;
    }

    public List<Long> getCertificateID(List<CertificateEntity> certificateEntityList){

        List<Long> idCert = new ArrayList<>();

        for(CertificateEntity c : certificateEntityList){
           idCert.add(c.getId_certificate());
        }

        return idCert;
    }
}
