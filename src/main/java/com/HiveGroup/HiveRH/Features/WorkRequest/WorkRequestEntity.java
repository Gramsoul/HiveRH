package com.HiveGroup.HiveRH.Features.WorkRequest;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_request")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WorkRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_work_request;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private WorkRequestType requestType;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "conpensation_description", length = 500)
    private String conpensationDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_account_id")
    private AccountEntity reviewedBy;

    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    @PrePersist
    private void prePersist() {
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }

        if (status == null) {
            status = RequestStatus.PENDING;
        }
    }
}
