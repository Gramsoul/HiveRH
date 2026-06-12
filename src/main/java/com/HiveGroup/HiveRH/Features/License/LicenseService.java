package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.Exceptions.EntityNotFoundException;
import com.HiveGroup.HiveRH.Features.Certificate.CertificateService;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseFilterDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import com.HiveGroup.HiveRH.Features.License.DTO.RequestLicenseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<LicenseDTO> getAllLicenseDTO(LicenseFilterDTO filters) {
        LicenseFilterDTO activeFilters = filters != null
                ? filters
                : new LicenseFilterDTO(null, null, null, null);

        return licenseRepository.findAll().stream()
                .filter(license -> activeFilters.idEmployee() == null
                        || license.getEmployee().getId_employee().equals(activeFilters.idEmployee()))
                .filter(license -> activeFilters.isAccepted() == null
                        || license.isAccepted() == activeFilters.isAccepted())
                .filter(license -> matchesDateRange(license, activeFilters))
                .map(this::toFullDTO)
                .toList();
    }

    @Transactional
    public LicenseDTO patchLicense(LicenseDTO licenseDTO) {
        LicenseEntity ori = licenseRepository.findById(licenseDTO.getId()).orElseThrow(() -> new EntityNotFoundException("Licencia no entrada","License"));

        if (licenseDTO.getIdEmployee() != null) {
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
                .idCertificates(certificateService.getCertificateID(ori.getCertificates()))
                .idEmployee(ori.getEmployee().getId_employee())
                .build();
    }

    @Transactional
    public LicenseDTO createLicense(RequestLicenseDTO license){
        if (license.idEmployee() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
        if (license.startDate() == null || license.endDate() == null) {
            throw new IllegalArgumentException("Las fechas de licencia son obligatorias");
        }
        if (license.endDate().isBefore(license.startDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        EmployeeEntity e = employeeRepository.findById(license.idEmployee()).orElseThrow(() -> new EntityNotFoundException("Empleado no entrada","Employee"));
        LicenseEntity licenseEntity = LicenseEntity.builder()
                .employee(e)
                .requestDate(license.requestDate() != null ? license.requestDate() : LocalDate.now())
                .startDate(license.startDate())
                .endDate(license.endDate())
                .isPaid(Boolean.TRUE.equals(license.isPaid()))
                .motive(license.motive())
                .description(license.description())
                .build();
        if (license.idCertificates() != null) {
            licenseEntity.setCertificates(certificateService.getCertificates(license.idCertificates()));
        }
        return toFullDTO(licenseRepository.save(licenseEntity));
    }

    public void deleteLicense(Long id) {
        LicenseEntity license = licenseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Licencia no entrada", "License"));
        licenseRepository.delete(license);
    }

    public LicenseDTO getLicense(Long id){
        LicenseEntity ent = licenseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Licencia no entrada","License"));

        return LicenseDTO.builder()
                .id(ent.getId_license())
                .requestDate(ent.getRequestDate())
                .isAccepted(ent.isAccepted())
                .startDate(ent.getStartDate())
                .endDate(ent.getEndDate())
                .isPaid(ent.isPaid())
                .motive(ent.getMotive())
                .description(ent.getDescription())
                .idCertificates(certificateService.getCertificateID(ent.getCertificates()))
                .idEmployee(ent.getEmployee().getId_employee())
                .build();
    }

    private boolean matchesDateRange(LicenseEntity license, LicenseFilterDTO filters) {
        if (filters.startDate() == null && filters.endDate() == null) {
            return true;
        }

        boolean startsBeforeFilterEnd = filters.endDate() == null || !license.getStartDate().isAfter(filters.endDate());
        boolean endsAfterFilterStart = filters.startDate() == null || !license.getEndDate().isBefore(filters.startDate());

        return startsBeforeFilterEnd && endsAfterFilterStart;
    }

    private LicenseDTO toFullDTO(LicenseEntity license) {
        return LicenseDTO.builder()
                .id(license.getId_license())
                .requestDate(license.getRequestDate())
                .isAccepted(license.isAccepted())
                .startDate(license.getStartDate())
                .endDate(license.getEndDate())
                .isPaid(license.isPaid())
                .motive(license.getMotive())
                .description(license.getDescription())
                .idCertificates(certificateService.getCertificateID(license.getCertificates()))
                .idEmployee(license.getEmployee().getId_employee())
                .build();
    }

}
