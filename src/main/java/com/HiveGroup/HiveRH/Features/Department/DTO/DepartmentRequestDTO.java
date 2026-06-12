package com.HiveGroup.HiveRH.Features.Department.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DepartmentRequestDTO(
        @JsonAlias("departmentName")
        String name
) {
}
