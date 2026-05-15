package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;

import java.time.LocalDate;

public record EmployeeCreateDTO(
        String name,
        String lastName,
        String phoneNumber,
        GenreEnum genre,
        String dni,
        String city,
        String address,
        LocalDate birth_date,
        LocalDate hire_date,
        Double base_salary,
        Long id_branch,
        Long id_position,
        Long id_department
) {
}
