package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeFilterDTO(
        String fullName,
        String dni,
        Long id_branch,
        LocalDate hire_date,
        LocalDate termination_date,
        EmployeeStatus status,
        String position,
        String department,
        Double min_salary,
        Double max_salary
) {}
