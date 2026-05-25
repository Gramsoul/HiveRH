package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Certificate.CertificateEntity;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeNotFoundException;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class LicenseService {
    public LicenseRepository licenseRepository;
    public EmployeeRepository employeeRepository;
    public CertificateService certificateService;
    public LicenseMapper licenseMapper;


    public LicenseDTO getDTO(LicenseEntity licence) {
        return licenseMapper.toDTO(licence);
    }

    public LicenseEntity getEntity(LicenseDTO license) {
        return licenseMapper.toEntity(license);
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

    @Transactional
    public LicenseDTO patchLicense(LicenseDTO licenseDTO) throws LicenseNotFoundException, EmployeeNotFoundException {
        LicenseEntity ori = licenseRepository.findById(licenseDTO.getId()).orElseThrow(() -> new LicenseNotFoundException(""));

        if (licenseDTO.getId() != null) {
            EmployeeEntity employee = employeeRepository
                    .findById(licenseDTO.getIdEmployee())
                    .orElseThrow(() -> new EmployeeNotFoundException(""));

            ori.setEmployee(employee);
        }
        if (licenseDTO.getIdCertificates() != null) {
            ori.setCertificates(certificateService
                    .getCertificates(licenseDTO.getIdCertificates()));
        }
        if (licenseDTO.getRequestDate() != null) ori.setRequestDate(licenseDTO.getRequestDate());
        if (licenseDTO.getIsAccepted()  != null) ori.setAccepted(licenseDTO.getIsAccepted());
        if (licenseDTO.getStartDate()   != null) ori.setStartDate(licenseDTO.getStartDate());
        if (licenseDTO.getEndDate()     != null) ori.setEndDate(licenseDTO.getEndDate());
        if (licenseDTO.getIsPaid()      != null) ori.setPaid(licenseDTO.getIsPaid());
        if (licenseDTO.getMotive()      != null) ori.setMotive(licenseDTO.getMotive());
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

    @Transactional
    public LicenseDTO createLicense(LicenseDTO licenseDTO) throws EmployeeNotFoundException {
        EmployeeEntity e = employeeRepository.findById(licenseDTO.getIdEmployee()).orElseThrow(() -> new EmployeeNotFoundException(""));
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

    public LicenseDTO getLicense(Long id) throws LicenseNotFoundException {
        LicenseEntity ent = licenseRepository.findById(id).orElseThrow(() -> new LicenseNotFoundException(""));

        List<Long> idCers = ent.getCertificates()
                .stream()
                .map(CertificateEntity::getId_certificate)
                .toList();

        return LicenseDTO.builder()
                .id(ent.getId_license())
                .requestDate(ent.getRequestDate())
                .isAccepted(ent.isAccepted())
                .startDate(ent.getStartDate())
                .endDate(ent.getEndDate())
                .isPaid(ent.isPaid())
                .motive(ent.getMotive())
                .description(ent.getDescription())
                .idCertificates(idCers)
                .idEmployee(ent.getEmployee().getId_employee())
                .build();
    }

}
