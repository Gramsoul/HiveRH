package com.HiveGroup.HiveRH.Features.Department.DTO;

public record DepartmentResponseDTO(
        Long id_department,
        String name,
        boolean active
) {
}
