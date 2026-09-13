package com.HiveGroup.HiveRH.Features.EmployeeAssignment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record EmployeeAssignmentDTO(
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long branchId,
         String branchName,
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long departmentId,
         String departmentName,
         @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
         Long positionId,
         String positionName,
         LocalDate startDate,
         LocalDate endDate,
         boolean active
) {}
