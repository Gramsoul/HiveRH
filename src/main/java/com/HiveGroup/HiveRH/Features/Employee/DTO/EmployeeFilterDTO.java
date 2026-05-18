package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;

import java.time.LocalDate;

public record EmployeeFilterDTO(
        Long id_branch,
        LocalDate hire_date,
        LocalDate termination_date,
        StatusEnum status,
        String position,
        String department,
        Double min_salary,
        Double max_salary
) {}