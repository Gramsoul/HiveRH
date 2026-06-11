package com.HiveGroup.HiveRH.Features.Department;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentRepository extends JpaRepository<@NonNull DepartmentEntity, @NonNull Long> {
    List<DepartmentEntity> findAllByIsActiveTrue();
}
