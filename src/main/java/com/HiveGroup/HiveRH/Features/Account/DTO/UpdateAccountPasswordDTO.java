package com.HiveGroup.HiveRH.Features.Account.DTO;

public record UpdateAccountPasswordDTO(
        String currentPassword,
        String newPassword
) {
}
