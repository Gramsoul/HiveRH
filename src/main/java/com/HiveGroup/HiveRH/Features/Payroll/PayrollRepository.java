package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {

    boolean existsByEmployeeAndPayrollDateBetween(
            EmployeeEntity employee,
            LocalDate startDate,
            LocalDate endDate
    );
}