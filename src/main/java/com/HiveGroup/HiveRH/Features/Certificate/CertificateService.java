package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

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
}
