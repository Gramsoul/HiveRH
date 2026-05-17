package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Certificate.CertificateEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LicenseService {
    public LicenseRepository licenseRepository;

    public LicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    public List<LicenseDTO> getAllLicenseDTO() {
        List<LicenseDTO> list = new ArrayList<>();

        licenseRepository.findAll().forEach(e -> {
            List<Long> certificateIDs = e.getCertificates()
                    .stream()
                    .map(CertificateEntity::getId_certificate)
                    .toList();


            list.add(LicenseDTO.builder()
                    .id(e.getId_license())
                    .requestDate(e.getRequestDate())
                    .isAccepted(e.isAccepted())
                    .startDate(e.getStartDate())
                    .endDate(e.getEndDate())
                    .isPaid(e.isPaid())
                    .motive(e.getMotive())
                    .description(e.getDescription())
                    .idCertificates(certificateIDs)
                    .idEmployee(e.getEmployee().getId_employee())
                    .build()
            );
        });
        return list;
    }

}
