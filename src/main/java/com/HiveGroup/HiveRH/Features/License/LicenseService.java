package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Certificate.CertificateRepository;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeNotFoundException;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LicenseService {
    public LicenseRepository licenseRepository;
    public EmployeeRepository employeeRepository;
    public CertificateRepository certificateRepository;

    public CertificateService certificateService;

    public LicenseService(LicenseRepository licenseRepository,
                          EmployeeRepository employeeRepository,
                          CertificateRepository certificateRepository,
                          CertificateService certificateService) {
        this.employeeRepository = employeeRepository;
        this.licenseRepository = licenseRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
    }

    public List<LicenseDTO> getAllLicenseDTO() {
        List<LicenseDTO> list = new ArrayList<>();

        licenseRepository.findAll().forEach(e -> {
            list.add(LicenseDTO.builder()
                    .id(e.getId_license())
                    .requestDate(e.getRequestDate())
                    .isAccepted(e.isAccepted())
                    .startDate(e.getStartDate())
                    .endDate(e.getEndDate())
                    .isPaid(e.isPaid())
                    .motive(e.getMotive())
                    .description(e.getDescription())
                    .idCertificates(
                            certificateService.getCertificateID(e.getCertificates())
                    )
                    .idEmployee(e.getEmployee().getId_employee())
                    .build()
            );
        });
        return list;
    }

    public LicenseDTO patchLicense(LicenseDTO licenseDTO) throws LicenseNotFoundException, EmployeeNotFoundException {
        LicenseEntity ori = licenseRepository.findById(licenseDTO.getId()).orElse(null);

        if (ori == null) throw new LicenseNotFoundException("id license don't exist");

        if (licenseDTO.getIdEmployee() != null) {
            EmployeeEntity employee = employeeRepository
                    .findById(licenseDTO.getIdEmployee())
                    .orElse(null);

            if (employee == null) throw new EmployeeNotFoundException("");
            else ori.setEmployee(employee);
        }

        if (licenseDTO.getIdCertificates() != null) {
            ori.setCertificates(
                    certificateService.getCertificates(licenseDTO.getIdCertificates())
            );
        }

        if (licenseDTO.getRequestDate() != null) ori.setRequestDate(licenseDTO.getRequestDate());

        if (licenseDTO.getIsAccepted() != null) ori.setAccepted(licenseDTO.getIsAccepted());

        if (licenseDTO.getStartDate() != null) ori.setStartDate(licenseDTO.getStartDate());

        if (licenseDTO.getEndDate() != null) ori.setEndDate(licenseDTO.getEndDate());

        if (licenseDTO.getIsPaid() != null) ori.setPaid(licenseDTO.getIsPaid());

        if (licenseDTO.getMotive() != null) ori.setMotive(licenseDTO.getMotive());

        if (licenseDTO.getDescription() != null) ori.setDescription(licenseDTO.getDescription());

        licenseRepository.save(ori);

        return LicenseDTO.builder()
                .id(licenseDTO.getId())
                .requestDate(ori.getRequestDate())
                .isAccepted(ori.isAccepted())
                .startDate(ori.getStartDate())
                .endDate(ori.getEndDate())
                .isPaid(ori.isPaid())
                .motive(ori.getMotive())
                .description(ori.getDescription())
                .idCertificates(licenseDTO.getIdCertificates())
                .idEmployee(licenseDTO.getIdEmployee())
                .build();
    }

    public LicenseDTO createLicense(LicenseDTO licenseDTO, EmployeeEntity e) {
        LicenseEntity licenseEntity = LicenseEntity.builder()
                .employee(e)
                .requestDate(licenseDTO.getRequestDate())
                .startDate(licenseDTO.getStartDate())
                .endDate(licenseDTO.getEndDate())
                .isPaid(licenseDTO.getIsPaid())
                .motive(licenseDTO.getMotive())
                .description(licenseDTO.getDescription())
                .build();

        licenseRepository.save(licenseEntity);

        licenseDTO.setId(licenseEntity.getId_license());

        return licenseDTO;

    }

}
