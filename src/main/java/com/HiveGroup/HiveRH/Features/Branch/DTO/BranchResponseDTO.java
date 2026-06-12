package com.HiveGroup.HiveRH.Features.Branch.DTO;

public record BranchResponseDTO(
        Long id_branch,
        String name,
        String city,
        String address,
        boolean active
) {
}
