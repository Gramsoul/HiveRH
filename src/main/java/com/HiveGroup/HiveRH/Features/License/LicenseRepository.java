package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepository extends JpaRepository<@NonNull LicenseEntity, @NonNull Long> {
    List<LicenseEntity> findByEmployee(EmployeeEntity employee);
}
