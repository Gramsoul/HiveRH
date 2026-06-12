package com.HiveGroup.HiveRH.Features.Employee;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends JpaRepository<@NonNull EmployeeEntity, @NonNull Long>{

}
