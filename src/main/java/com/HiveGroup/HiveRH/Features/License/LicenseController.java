package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Certificate.CertificateEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LicenseController {
    LicenseService licenseService;
    LicenseRepository licenseRepository;
    EmployeeRepository employeeRepository;

    public LicenseController(LicenseService licenseService, LicenseRepository licenseRepository, EmployeeRepository employeeRepository) {
        this.licenseService = licenseService;
        this.licenseRepository = licenseRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/api/license")
    public ResponseEntity<List<LicenseDTO>> getLicenses() {

        return ResponseEntity.ok().body(
                licenseService.getAllLicenseDTO()
        );
    }

    @GetMapping("/api/license/{id_license}")
    public ResponseEntity<LicenseDTO> getVacationByID(@PathVariable @NotNull Long id_license) {
        LicenseEntity ent = licenseRepository.findById(id_license).orElse(null);

        if (ent == null) {
            return ResponseEntity.notFound().build();
        } else {
            List<Long> idCers = ent.getCertificates()
                    .stream()
                    .map(CertificateEntity::getId_certificate)
                    .toList();

            LicenseDTO dto = LicenseDTO.builder()
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


            return ResponseEntity.ok().body(dto);
        }
    }

    @PostMapping("/api/license")
    public ResponseEntity<LicenseDTO> postLicense(@RequestBody @NotNull LicenseDTO license) {
        EmployeeEntity e = employeeRepository.findById(license.getIdEmployee()).orElse(null);
        if (e == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok().body(licenseService.createLicense(license, e));
        }
    }

    @PatchMapping("/api/license")
    public ResponseEntity<LicenseDTO> patchLicense(@RequestBody @NotNull LicenseDTO license) {
        LicenseEntity lic = licenseRepository.findById(license.getId()).orElse(null);

        if (lic == null) {
            return ResponseEntity.notFound().build();
        } else {
            LicenseDTO lc = licenseService.patchLicense(license);
            return ResponseEntity.ok().body(lc);
        }
    }
}
