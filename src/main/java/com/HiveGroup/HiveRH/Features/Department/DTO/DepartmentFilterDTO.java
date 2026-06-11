package com.HiveGroup.HiveRH.Features.Department.DTO;

public record DepartmentFilterDTO(
        Long id_department,
        String name,
        Boolean active
) {
}
