package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;

import java.time.LocalDate;

public record EmployeeUpdateDTO(
        String name,
        String lastName,
        String phoneNumber,
        GenreEnum genre,
        String dni,
        String city,
        String address,
        LocalDate birth_date,
        LocalDate hire_date,
        LocalDate termination_date,
        StatusEnum status,
        Double base_salary,
        Long id_account,
        Long id_branch
) {
}
