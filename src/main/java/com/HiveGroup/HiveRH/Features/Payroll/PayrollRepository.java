package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {

    List<PayrollEntity> findByEmployeeAndPayrollDateBetween(
            EmployeeEntity employee,
            LocalDate startDate,
            LocalDate endDate
    );
}
