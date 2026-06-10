package com.HiveGroup.HiveRH.Features.Branch.DTO;

public record BranchUpdateDTO(
        String name,
        String city,
        String address,
        Boolean active
) {
}
