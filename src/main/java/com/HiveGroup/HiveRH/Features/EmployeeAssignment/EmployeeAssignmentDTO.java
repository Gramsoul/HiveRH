package com.HiveGroup.HiveRH.Features.EmployeeAssignment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmployeeAssignmentDTO(
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long departmentId,
         String departmentName,
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long positionId,
         String positionName
) {}
