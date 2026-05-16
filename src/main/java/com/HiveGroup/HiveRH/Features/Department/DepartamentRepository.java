package com.HiveGroup.HiveRH.Features.Department;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentRepository extends JpaRepository<@NonNull DepartmentEntity, @NonNull Long> {
}
