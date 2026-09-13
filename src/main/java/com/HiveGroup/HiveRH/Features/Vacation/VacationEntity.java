package com.HiveGroup.HiveRH.Features.Vacation;


import com.HiveGroup.HiveRH.Common.Utils.Enums.AbsenceStatus;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vacation")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VacationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_vacation;

    @Column (name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AbsenceStatus status = AbsenceStatus.PENDING;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_account_id")
    private AccountEntity reviewedBy;

    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
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
