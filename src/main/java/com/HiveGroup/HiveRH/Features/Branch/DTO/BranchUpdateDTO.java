package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;

public record BranchUpdateDTO(
        @JsonAlias("branchName")
        @Schema(description = "Branch display name.", example = "Downtown Branch")
        String name,

        @Schema(description = "City where the branch is located.", example = "Cordoba")
        String city,

        @Schema(description = "Branch street address.", example = "Av. Colon 123")
        String address,

        @Schema(description = "Whether the branch is active.", example = "true")
        Boolean active
) {
}
