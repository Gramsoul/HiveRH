package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public record BranchCreateDTO(
        @JsonAlias("branchName")
        String name,
        String city,
        String address
) {
}
