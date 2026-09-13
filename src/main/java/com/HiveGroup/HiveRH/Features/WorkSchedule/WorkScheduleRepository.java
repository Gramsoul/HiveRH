package com.HiveGroup.HiveRH.Features.WorkSchedule;

import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkScheduleStatus;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<@NonNull WorkScheduleEntity, @NonNull Long> {

    List<WorkScheduleEntity> findByEmployee(EmployeeEntity employee);

    List<WorkScheduleEntity> findByEmployeeAndWorkDateAndStatus(
            EmployeeEntity employee,
            LocalDate workDate,
            WorkScheduleStatus status
    );
}
