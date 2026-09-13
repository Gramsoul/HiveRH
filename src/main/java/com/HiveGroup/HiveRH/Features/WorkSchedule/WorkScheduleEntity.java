package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleType;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_schedule")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WorkScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_work_schedule;

    @ManyToOne
    @JoinColumn(name = "id_employee", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WorkScheduleType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private WorkScheduleStatus status = WorkScheduleStatus.ACTIVE;

    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne
    @JoinColumn(name = "createdby", nullable = false)
    private AccountEntity createdBy;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = WorkScheduleStatus.ACTIVE;
        }
    }
}
