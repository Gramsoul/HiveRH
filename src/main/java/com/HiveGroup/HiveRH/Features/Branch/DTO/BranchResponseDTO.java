package com.HiveGroup.HiveRH.Features.Branch.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record BranchResponseDTO(
        @Schema(example = "1")
        Long id_branch,

        @Schema(example = "Downtown Branch")
        String name,

        @Schema(example = "Cordoba")
        String city,

        @Schema(example = "Av. Colon 123")
        String address,

        @Schema(example = "true")
        boolean active
) {
}
