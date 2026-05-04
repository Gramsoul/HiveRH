package com.HiveGroup.HiveRH.Features.EmployeeAssignment;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeAssignmentRepository extends JpaRepository<@NonNull EmployeeAssignmentEntity, @NonNull Long> {
}
