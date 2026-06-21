package com.HiveGroup.HiveRH.Features.Department.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DepartmentResponseDTO(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id_department,
        String name,
        boolean active
) {
}
