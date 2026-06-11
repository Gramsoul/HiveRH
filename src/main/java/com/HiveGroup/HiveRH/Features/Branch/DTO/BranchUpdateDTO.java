package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public record BranchUpdateDTO(
        @JsonAlias("branchName")
        String name,
        String city,
        String address,
        Boolean active
) {
}
