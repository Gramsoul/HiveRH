package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BranchUpdateDTO(
        @JsonAlias("branchName")

        String name,

        String city,

        String address,

        Boolean active
) {
}
