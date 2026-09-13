package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AbsenceStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "paid")
    private boolean isPaid;

    @Column(name = "motive", length = 300)
    private String motive;

    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL)
    //@JsonManagedReference
    private List<CertificateEntity> certificates;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_account_id")
    private AccountEntity reviewedBy;

    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    //@JsonBackReference
    private EmployeeEntity employee;

    @PrePersist
    private void prePersist() {
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }

        if (status == null) {
            status = AbsenceStatus.PENDING;
        }
    }
}
