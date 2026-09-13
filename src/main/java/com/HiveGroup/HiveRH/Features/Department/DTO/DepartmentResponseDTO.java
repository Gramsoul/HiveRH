package com.HiveGroup.HiveRH.Features.Department.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepartmentResponseDTO(
        Long id_department,
        String name,
        boolean active
) {
}
