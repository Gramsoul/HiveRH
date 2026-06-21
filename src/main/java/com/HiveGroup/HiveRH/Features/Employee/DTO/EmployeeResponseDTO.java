package com.HiveGroup.HiveRH.Features.Employee.DTO;

import com.HiveGroup.HiveRH.Common.Utils.Enums.GenreEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Features.EmployeeAssignment.EmployeeAssignmentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record EmployeeResponseDTO(
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
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long branch_id,
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long account_id,
         List<EmployeeAssignmentDTO> assignments
) {}
