package com.HiveGroup.HiveRH.Features.Complaint;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<@NonNull ComplaintEntity, @NonNull Long> {

    List<ComplaintEntity> findByEmployee(EmployeeEntity employee);

    List<ComplaintEntity> findByStatus(ComplaintStatusEnum status);

}