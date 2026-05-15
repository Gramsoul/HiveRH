package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentDTO;

import java.time.LocalDate;
import java.util.List;

public record EmployeeResponseDTO(
         Long id_employee,
         String name,
         String lastName,
         String phoneNumber,
         GenreEnum genre,
         String dni,
         String city,
         String address,
         LocalDate birthdate,
         LocalDate hireDate,
         LocalDate terminationDate,
         double baseSalary,
         StatusEnum status,
         Long branch_id,
         Long account_id,
         List<EmployeeAssignmentDTO> assignments
) {}
