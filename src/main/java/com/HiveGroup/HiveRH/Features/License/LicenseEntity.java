package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Certificate.CertificateEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "license")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LicenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_license;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "accepted")
    private boolean isAccepted;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "paid")
    private boolean isPaid;

    @Column(name = "motive", length = 300)
    private String motive;

    @Column(name = "description", length = 200)
    private String description;

    @OneToMany(mappedBy = "license")
    private List<CertificateEntity> certificates;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;
}
