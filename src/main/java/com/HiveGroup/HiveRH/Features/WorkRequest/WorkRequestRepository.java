package com.HiveGroup.HiveRH.Features.WorkRequest;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RequestStatus;
import com.HiveGroup.HiveRH.Common.Utils.Enums.WorkRequestType;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkRequestRepository extends JpaRepository<@NonNull WorkRequestEntity, @NonNull Long> {

    List<WorkRequestEntity> findByEmployee(EmployeeEntity employee);

    List<WorkRequestEntity> findByEmployeeAndRequestTypeAndTargetDateAndStatus(
            EmployeeEntity employee,
            WorkRequestType requestType,
            LocalDate targetDate,
            RequestStatus status
    );
}
