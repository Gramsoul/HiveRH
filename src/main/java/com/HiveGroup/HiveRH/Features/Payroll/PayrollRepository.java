package com.HiveGroup.HiveRH.Features.Payroll;

import com.HiveGroup.HiveRH.Common.Utils.Enums.PayrollStatus;
import com.HiveGroup.HiveRH.Features.Employee.EmployeeEntity;
import com.HiveGroup.HiveRH.Features.PayrollPeriod.PayrollPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<PayrollEntity, Long> {

    List<PayrollEntity> findByEmployee(EmployeeEntity employee);

    List<PayrollEntity> findByEmployeeAndStatus(EmployeeEntity employee, PayrollStatus status);

    List<PayrollEntity> findByEmployeeAndPeriod(EmployeeEntity employee, PayrollPeriodEntity period);

    boolean existsByPeriodAndStatus(PayrollPeriodEntity period, PayrollStatus status);
}
