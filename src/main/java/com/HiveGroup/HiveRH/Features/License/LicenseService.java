package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateEntity;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.ResponseLicenseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class LicenseService {
    public LicenseRepository licenseRepository;
    public EmployeeRepository employeeRepository;
    public CertificateService certificateService;
    @Autowired
    public LicenseMapper licenseMapper;


    public LicenseDTO getDTO(LicenseEntity licence) {
        //return licenseMapper.toDTO(licence);
        return LicenseDTO.builder()
                .id(licence.getId_license())
                .startDate(licence.getStartDate())
                .motive(licence.getMotive())
                .endDate(licence.getEndDate())
                .idEmployee(licence.getEmployee().getId_employee())
                .description(licence.getDescription())
                .build();
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
    public LicenseDTO patchLicense(LicenseDTO licenseDTO) {
        LicenseEntity ori = licenseRepository.findById(licenseDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Licencia no entrada","License"));

        if (licenseDTO.getId() != null) {
            EmployeeEntity employee = employeeRepository
                    .findById(licenseDTO.getIdEmployee())
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found", "Employee"));

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
    public ResponseLicenseDTO createLicense(RequestLicenseDTO license){
        EmployeeEntity e = employeeRepository.findById(license.idEmployee()).orElseThrow(() -> new EntityNotFoundException("Empleado no entrada","Employee"));
        LicenseEntity licenseEntity = LicenseEntity.builder()
                .employee(e)
                .requestDate(license.requestDate())
                .isAccepted(Boolean.TRUE.equals(license.isAccepted()))
                .startDate(license.startDate())
                .endDate(license.endDate())
                .isPaid(Boolean.TRUE.equals(license.isPaid()))
                .motive(license.motive())
                .description(license.description())
                .build();
        LicenseEntity saved = licenseRepository.save(licenseEntity);

        return ResponseLicenseDTO.builder()
                .id(saved.getId_license())
                .isAccepted(saved.isAccepted())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .isPaid(saved.isPaid())
                .motive(saved.getMotive())
                .description(saved.getDescription())
                .idEmployee(saved.getEmployee().getId_employee())
                .build();
    }

    public LicenseDTO getLicense(Long id){
        LicenseEntity ent = licenseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Licencia no entrada","License"));

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
