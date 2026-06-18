package com.HiveGroup.HiveRH.Features.Branch.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BranchResponseDTO(
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Long id_branch,
        String name,
        String city,
        String address,
        boolean active
) {
}
